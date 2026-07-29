package com.deepcraft.stage0.day1;

/**
 * Day 1：第一个程序。
 *
 * JDK / JRE / JVM 的关系：
 *   JVM  = 运行 .class 字节码的虚拟机（一次编译，到处运行的关键）
 *   JRE  = JVM + 运行所需的核心类库（只够"跑"）
 *   JDK  = JRE + 开发工具（javac、javadoc、jstack……够"开发"）
 *
 * 本类的两种运行方式：
 *   1. 传统：javac HelloWorld.java && java HelloWorld   （编译产生 .class 再运行）
 *   2. JDK 11+ 单文件源码模式：java HelloWorld.java     （内存中编译，直接运行）
 */
public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello, Java " + System.getProperty("java.version"));
    }
}
