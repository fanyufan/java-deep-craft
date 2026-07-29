package com.deepcraft.stage0.day7;

/** 底层依赖：模拟数据访问层 */
public class UserRepository {

    public String findNameById(long id) {
        return "user-" + id;
    }
}
