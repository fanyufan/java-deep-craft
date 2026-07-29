package com.deepcraft.stage0.day7;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Day 7：自定义注入注解。
 *
 * @Target(FIELD)      只能标在字段上
 * @Retention(RUNTIME) 保留到运行时，反射才读得到（CLASS/SOURCE 都读不到）
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
