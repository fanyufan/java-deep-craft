package com.deepcraft.stage0.day7;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Day 7：极简容器 —— 创建对象，并把 @Inject 标注的字段用反射填上。
 *
 * 这是阶段 4 "手写 mini IoC" 的种子。它缺的东西（单例缓存、构造器注入、
 * 接口到实现的绑定、循环依赖检测）正是 Spring IoC 要解决的，到时候对照体会。
 */
public class Container {

    public static <T> T getInstance(Class<T> clazz) {
        try {
            // 1. 通过无参构造创建实例
            Constructor<T> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            T instance = ctor.newInstance();

            // 2. 扫描字段，递归注入 @Inject 标注的依赖
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Object dependency = getInstance(field.getType());
                    field.setAccessible(true);
                    field.set(instance, dependency);
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("无法创建 " + clazz.getName(), e);
        }
    }
}
