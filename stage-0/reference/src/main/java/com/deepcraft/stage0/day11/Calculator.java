package com.deepcraft.stage0.day11;

/** Day 11：被测对象，一个故意写得很简单的计算器 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("除数不能为 0");
        }
        return a / b;
    }

    public boolean isEven(int n) {
        return n % 2 == 0;
    }
}
