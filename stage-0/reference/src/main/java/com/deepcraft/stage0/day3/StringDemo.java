package com.deepcraft.stage0.day3;

/**
 * Day 3：String 的三个高频考点。
 */
public class StringDemo {

    public static void main(String[] args) {
        equality();
        immutability();
        textBlock();
    }

    /** equals vs == ：== 比的是引用，equals 比的是内容 */
    static void equality() {
        String a = "hello";                 // 字面量，进字符串常量池
        String b = "hello";                 // 复用常量池中的同一个对象
        String c = new String("hello");     // new 强制在堆上建新对象

        System.out.println("a == b      -> " + (a == b));        // true（常量池复用）
        System.out.println("a == c      -> " + (a == c));        // false（不同对象）
        System.out.println("a.equals(c) -> " + a.equals(c));     // true（内容相同）
    }

    /** 不可变性：所有"修改"都产生新对象，原对象不变 —— 循环拼接要用 StringBuilder */
    static void immutability() {
        String s = "abc";
        s.concat("def");                    // 返回值被丢弃，s 没变！
        System.out.println("concat 后 s = " + s);                // abc

        // 循环里 += 字符串，每次都会 new 一个 StringBuilder，性能差
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i);
        }
        System.out.println("StringBuilder = " + sb);             // 01234
    }

    /** 文本块（Java 15）：告别满屏 \n 和转义 */
    static void textBlock() {
        String json = """
                {
                  "name": "deep-craft",
                  "jdk": 17
                }
                """;
        System.out.println("文本块 json = " + json);
    }
}
