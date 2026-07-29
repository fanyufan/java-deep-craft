package com.deepcraft.stage0.day7;

/** 上层依赖：声明需要注入 UserRepository，自己不 new */
public class UserService {

    @Inject
    private UserRepository userRepository;

    public String greet(long id) {
        // 如果注入失败这里就是 NPE —— 用它来验证容器工作正常
        return "hello, " + userRepository.findNameById(id);
    }
}
