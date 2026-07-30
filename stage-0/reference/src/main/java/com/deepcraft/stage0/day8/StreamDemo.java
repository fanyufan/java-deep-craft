package com.deepcraft.stage0.day8;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Day 8：函数式接口、Stream、Optional。
 */
public class StreamDemo {

    public static void main(String[] args) {
        functionalInterfaces();
        streamOps();
        optionalUsage();
    }

    /** 四个核心函数式接口：记住"输入输出"就记住了全部 */
    static void functionalInterfaces() {
        Function<String, Integer> length = String::length;   // 入 T 出 R
        Predicate<String> isLong = s -> s.length() > 3;      // 入 T 出 boolean
        Consumer<String> print = System.out::println;        // 入 T 无返回
        Supplier<String> defaultName = () -> "anonymous";    // 无入出 T

        System.out.println("length(\"java\") = " + length.apply("java"));
        System.out.println("isLong(\"go\") = " + isLong.test("go"));
        print.accept("Consumer 在打印我");
        System.out.println("Supplier 给出: " + defaultName.get());
    }

    static void streamOps() {
        List<String> words = List.of("apple", "banana", "avocado", "cherry", "apricot");

        // filter + map + collect：最经典的三段式
        List<String> result = words.stream()
                .filter(w -> w.startsWith("a"))
                .map(String::toUpperCase)
                .toList();                          // Java 16+，等价 collect(Collectors.toList())
        System.out.println("a 开头转大写 = " + result);

        // groupingBy：按首字母分组
        Map<String, List<String>> byFirst = words.stream()
                .collect(Collectors.groupingBy(w -> w.substring(0, 1)));
        System.out.println("按首字母分组 = " + byFirst);

        // flatMap：把"流的流"拍平
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5));
        List<Integer> flat = nested.stream().flatMap(List::stream).toList();
        System.out.println("flatMap 拍平 = " + flat);

        // 统计终端操作
        long count = words.stream().filter(w -> w.length() > 5).count();
        System.out.println("长度>5 的个数 = " + count);
    }

    /** Optional 的正确姿势：只做返回值，链式消费，别 isPresent + get */
    static void optionalUsage() {
        // 反面教材（注释掉，别这么写）：
        // if (opt.isPresent()) { opt.get() ... }  —— 跟判 null 没区别，白用了 Optional

        String name = findName(false)
                .map(String::toUpperCase)              // 有值则变换
                .orElse("DEFAULT");                    // 没值给兜底
        System.out.println("orElse 结果 = " + name);

        // orElse vs orElseGet：orElse 的参数是"立即求值"的，没值也会执行
        findName(true).orElse(expensiveDefault());       // 这行会打印！尽管有值
        findName(true).orElseGet(() -> expensiveDefault()); // 有值时不会执行，省开销

        // 值缺失且必须存在时：orElseThrow
        try {
            findName(false).orElseThrow(() -> new IllegalStateException("名字不存在"));
        } catch (IllegalStateException e) {
            System.out.println("orElseThrow 抛出: " + e.getMessage());
        }
    }

    static Optional<String> findName(boolean exists) {
        return exists ? Optional.of("tom") : Optional.empty();
    }

    static String expensiveDefault() {
        System.out.println("  -> expensiveDefault() 被执行了");
        return "DEFAULT";
    }
}
