/**
 * sealed 体系演示：final / sealed / non-sealed 三种实现类。
 *
 * 编译运行（JDK 17+；Windows 中文环境需指定 -encoding UTF-8）：
 *   javac -encoding UTF-8 ShapeDemo.java
 *   java ShapeDemo
 */
public class ShapeDemo {

    // ========== sealed 接口：permits 列出允许的实现类 ==========
    sealed interface Shape permits Circle, Rectangle, FreeShape {
    }

    // ---------- 1. final：叶子节点，继承链到此终结 ----------
    static final class Circle implements Shape {
        final double radius;

        Circle(double radius) {
            this.radius = radius;
        }
    }

    // ---------- 2. sealed：继续管控，只有 permits 点名的能继承 ----------
    static sealed class Rectangle implements Shape permits Square {
        final double width;
        final double height;

        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }
    }

    // Rectangle 的唯一合法子类，自身用 final 终结
    static final class Square extends Rectangle {
        Square(double side) {
            super(side, side);
        }
    }

    // ---------- 3. non-sealed：放弃管控，重新开放继承 ----------
    static non-sealed class FreeShape implements Shape {
        final String sketch;

        FreeShape(String sketch) {
            this.sketch = sketch;
        }
    }

    // FreeShape 是 non-sealed，所以任何人都能继承，不在 permits 名单里也行
    static class MyDoodle extends FreeShape {
        MyDoodle() {
            super("随手涂鸦");
        }
    }

    // ========== instanceof 模式匹配（JDK 16+）==========
    // JDK 21+ 可以换成 switch 模式匹配，编译器会做穷尽性检查（少一个分支直接编译报错）
    static String describe(Shape s) {
        if (s instanceof Circle c) {
            return "圆形，半径 = " + c.radius;
        } else if (s instanceof Square sq) {
            return "正方形，边长 = " + sq.width;
        } else if (s instanceof Rectangle r) {
            return "矩形，" + r.width + " x " + r.height;
        } else if (s instanceof FreeShape f) {
            return "自由形状：" + f.sketch;
        }
        throw new IllegalStateException("未知 Shape 实现：" + s.getClass());
    }

    public static void main(String[] args) {
        System.out.println(describe(new Circle(2.0)));
        System.out.println(describe(new Square(3.0)));
        System.out.println(describe(new Rectangle(3.0, 4.0)));
        System.out.println(describe(new MyDoodle()));
    }
}
