package com.deepcraft.stage0.day9;

import java.util.List;

/**
 * Day 9（上）：var、record、switch 表达式。
 */
public class NewFeaturesDemo {

    public static void main(String[] args) {
        varDemo();
        recordDemo();
        switchExpression();
    }

    /** var：编译器推断类型，变量本身还是静态类型。适合"类型看一眼就知道"的场合 */
    static void varDemo() {
        var name = "deep-craft";                 // String，一目了然 —— 好
        var scores = List.of(90, 85, 77);        // List<Integer> —— 好
        // var x = process();                    // 鬼知道 process 返回什么 —— 差，别这么用
        // var 不能用于：字段、方法参数、返回类型、未初始化的声明

        System.out.println("var name = " + name + "，scores = " + scores);
    }

    /** record：为"不可变数据载体"而生。一行声明 = 构造器 + accessor + equals/hashCode + toString */
    record Point(int x, int y) {
        // 紧凑构造器：参数校验的标准位置
        Point {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("坐标不能为负: " + x + "," + y);
            }
        }

        double distanceToOrigin() {
            return Math.hypot(x, y);
        }
    }

    static void recordDemo() {
        var p1 = new Point(3, 4);
        var p2 = new Point(3, 4);

        System.out.println("toString  = " + p1);                    // Point[x=3, y=4]
        System.out.println("equals    = " + p1.equals(p2));         // true（按值比较）
        System.out.println("accessor  = x=" + p1.x() + " y=" + p1.y());  // 注意：是 x() 不是 getX()
        System.out.println("distance  = " + p1.distanceToOrigin()); // 5.0

        try {
            new Point(-1, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("校验生效: " + e.getMessage());
        }
    }

    /** switch 表达式（Java 14）：有返回值、箭头分支不穿透、必须穷尽 */
    static String parity(int n) {
        return switch (n % 2) {
            case 0 -> "偶数";
            case 1 -> "奇数";
            default -> {                 // 负数的 % 结果是负的，走这里
                String msg = "负奇数";
                yield msg;               // 多语句分支用 yield 返回值
            }
        };
    }

    static void switchExpression() {
        System.out.println("4 是 " + parity(4));
        System.out.println("7 是 " + parity(7));
        System.out.println("-3 是 " + parity(-3));

        // instanceof 模式匹配（Day 4 已见，这里串起来复习）
        Object obj = "hello";
        if (obj instanceof String s && s.length() > 3) {   // s 在 && 右侧直接可用
            System.out.println("是长度>3的字符串: " + s);
        }
    }
}
