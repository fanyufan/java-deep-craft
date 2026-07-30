package com.deepcraft.stage0.day11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Day 11：JUnit 5 核心用法。
 * 运行：mvn test，或 IDEA 里点方法旁的绿色箭头。
 */
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        // 每个测试方法执行前都会跑一遍 —— 测试之间互不影响
        calculator = new Calculator();
    }

    @Test
    @DisplayName("除法：正常相除")
    void divideNormally() {
        assertEquals(3, calculator.divide(10, 3));
    }

    @Test
    @DisplayName("除法：除数为 0 抛异常")
    void divideByZero() {
        ArithmeticException e = assertThrows(ArithmeticException.class,
                () -> calculator.divide(1, 0));
        assertEquals("除数不能为 0", e.getMessage());
    }

    @ParameterizedTest(name = "isEven({0}) 应为 true")
    @ValueSource(ints = {2, 0, -4, 100})
    void evenNumbers(int n) {
        assertTrue(calculator.isEven(n));
    }

    @ParameterizedTest(name = "isEven({0}) 应为 false")
    @ValueSource(ints = {1, -3, 99})
    void oddNumbers(int n) {
        assertFalse(calculator.isEven(n));
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({"1, 2, 3", "0, 0, 0", "-1, 1, 0", "100, 200, 300"})
    void addCases(int a, int b, int expected) {
        assertEquals(expected, calculator.add(a, b));
    }
}
