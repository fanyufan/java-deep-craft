package com.deepcraft.stage0.day5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Day 5：集合的"会用"层面。实现原理（哈希、扩容、树化）留给阶段 1 抠源码。
 */
public class CollectionDemo {

    public static void main(String[] args) {
        listBasics();
        mapBasics();
        setDedup();
        immutableCollections();
    }

    static void listBasics() {
        List<String> langs = new ArrayList<>(List.of("Java", "Go", "Python"));

        // 三种遍历方式
        for (String s : langs) {          // for-each：最常用
            System.out.println("for-each: " + s);
        }
        langs.forEach(s -> System.out.println("forEach : " + s));   // lambda
        langs.forEach(System.out::println);                          // 方法引用，等价上一行

        System.out.println("包含 Java? " + langs.contains("Java"));
    }

    static void mapBasics() {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Tom", 90);
        scores.merge("Tom", 10, Integer::sum);        // 存在则累加 -> 100
        scores.putIfAbsent("Tom", 0);                  // 已存在，不覆盖
        int jerry = scores.getOrDefault("Jerry", -1);  // 不存在给默认值，避免 NPE

        System.out.println("Tom = " + scores.get("Tom"));     // 100
        System.out.println("Jerry = " + jerry);               // -1

        // 词频统计的经典写法：computeIfAbsent / merge
        Map<String, Integer> wordCount = new HashMap<>();
        for (String w : "a b a c a b".split(" ")) {
            wordCount.merge(w, 1, Integer::sum);
        }
        System.out.println("词频 = " + wordCount);            // {a=3, b=2, c=1}
    }

    static void setDedup() {
        Set<String> set = new HashSet<>(List.of("a", "b", "a", "c"));
        System.out.println("去重后 = " + set);                // 3 个元素
    }

    /** 不可变集合：创建后任何修改都抛 UnsupportedOperationException */
    static void immutableCollections() {
        List<String> immutable = List.of("a", "b");
        try {
            immutable.add("c");
        } catch (UnsupportedOperationException e) {
            System.out.println("List.of 不可变，add 抛异常: " + e.getClass().getSimpleName());
        }

        // 想要"以不可变集合为基础的副本"，要显式 new 一个可变的
        List<String> mutableCopy = new ArrayList<>(immutable);
        mutableCopy.add("c");
        System.out.println("可变副本 = " + mutableCopy);
    }
}
