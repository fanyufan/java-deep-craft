package com.deepcraft.stage0.day7;

/**
 * Day 7：DI demo 入口。
 * 容器自动完成 UserService -> UserRepository 的依赖装配。
 */
public class DiDemo {

    public static void main(String[] args) {
        UserService service = Container.getInstance(UserService.class);
        System.out.println(service.greet(42));   // hello, user-42

        // 对照：反射不是免费的
        // - 每次 getInstance 都新建对象（没有单例）
        // - setAccessible 破坏了 private 封装（JDK 模块系统下可能受限）
        // - 没有类型检查，注入错类型要等到运行时才炸
    }
}
