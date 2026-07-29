package com.deepcraft.stage0.day4;

/**
 * Day 4：sealed class —— 受控的继承层次。
 *
 * 解决的问题：普通 interface 谁都能实现，编译器无法穷举所有实现类；
 * sealed 明确声明"只有这几个类能实现我"，编译器因此能做穷尽性检查，
 * 模式匹配时漏了分支会直接编译报错。
 *
 * 注意：sealed 本身是 Java 17 正式特性；但 switch 模式匹配在 17 是预览特性，
 * 所以这里用 instanceof 模式匹配（Java 16 正式）演示，Day 9 再完整展开。
 */
public class ShapeDemo {

    public static void main(String[] args) {
        Shape[] shapes = {new Circle(2.0), new Rectangle(3.0, 4.0)};
        for (Shape s : shapes) {
            System.out.println(describe(s) + "，面积 = " + s.area());
        }
    }

    /** instanceof 模式匹配：判断类型的同时完成绑定，省掉强转 */
    static String describe(Shape s) {
        if (s instanceof Circle c) {
            return "圆形(半径=" + c.radius + ")";
        } else if (s instanceof Rectangle r) {
            return "矩形(" + r.width + "x" + r.height + ")";
        }
        // 理论上到不了这里：sealed 保证只有两种实现。
        // 但 instanceof 链编译器不做穷尽检查，仍需兜底 —— 这正是 switch 模式匹配要改进的。
        throw new IllegalStateException("未知形状: " + s.getClass());
    }
}

/** sealed 接口：permits 列出允许的实现类，实现类必须是 final / sealed / non-sealed 之一 */
sealed interface Shape permits Circle, Rectangle {
    double area();
}

final class Circle implements Shape {
    final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

final class Rectangle implements Shape {
    final double width;
    final double height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }
}
