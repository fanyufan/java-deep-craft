package com.deepcraft.stage0.day9;

/**
 * Day 9（下）：sealed + record + switch 模式匹配 —— Java 17 的标志性写法。
 *
 * ⚠️ switch 模式匹配在 JDK 17 是【预览特性】，编译运行都要 --enable-preview：
 *   javac --enable-preview --release 17 ...
 *   java  --enable-preview ...
 * （本模块 pom 已配置，IDEA 里把 Project language level 设为 "17 (Preview)" 即可）
 *
 * 对比 Day 4 的 instanceof 链：sealed 让编译器知道 Expr 只有四种实现，
 * switch 因此能做【穷尽性检查】——漏掉任何一种直接编译报错，不再需要兜底分支。
 * 这就是"代数数据类型(ADT)"在 Java 里的样子，函数式语言的核心建模方式。
 */
public class ExprEvaluator {

    /** 表达式 = 数字 | 加法 | 乘法 | 取负。permits 省略则默认同包内所有实现 */
    sealed interface Expr permits Num, Add, Mul, Neg {
    }

    record Num(int value) implements Expr {
    }

    record Add(Expr left, Expr right) implements Expr {
    }

    record Mul(Expr left, Expr right) implements Expr {
    }

    record Neg(Expr inner) implements Expr {
    }

    /** 求值：每个分支解构出 record 组件直接用，没有强转、没有兜底 */
    static int eval(Expr expr) {
        return switch (expr) {
            case Num n -> n.value();
            case Add a -> eval(a.left()) + eval(a.right());
            case Mul m -> eval(m.left()) * eval(m.right());
            case Neg n -> -eval(n.inner());
        };
    }

    /** 打印：演示守卫模式做更细的分支。注意：JDK 17 预览版用 && 连接守卫条件，JDK 19+ 才改成 when 关键字 */
    static String render(Expr expr) {
        return switch (expr) {
            case Num n && n.value() < 0 -> "(" + n.value() + ")";
            case Num n -> String.valueOf(n.value());
            case Add a -> render(a.left()) + " + " + render(a.right());
            case Mul m -> "(" + render(m.left()) + ") * (" + render(m.right()) + ")";
            case Neg n -> "-(" + render(n.inner()) + ")";
        };
    }

    public static void main(String[] args) {
        // (1 + 2) * -(3)
        Expr expr = new Mul(new Add(new Num(1), new Num(2)), new Neg(new Num(3)));

        System.out.println("表达式: " + render(expr));
        System.out.println("结果  : " + eval(expr));    // -9

        // 试试注释掉 eval 里任意一个 case —— 编译器立刻报错提示不穷尽，
        // 这就是 sealed 的价值（Day 4 的 instanceof 链做不到这一点）。
    }
}
