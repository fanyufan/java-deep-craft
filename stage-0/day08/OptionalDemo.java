import java.util.Optional;

/**
 * Day 8：Optional 的正确用法 —— 只做返回值，不做字段和参数。
 *
 * Optional 的定位：一个"最多装一个值"的盒子，让"可能没有结果"这件事
 * 体现在类型签名上，逼着调用方处理空的情况，而不是被 NPE 偷袭。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 OptionalDemo.java
 *   java -Dfile.encoding=UTF-8 OptionalDemo
 */
public class OptionalDemo {

    public static void main(String[] args) {
        createAndUnwrap();
        chaining();
        correctUsageBoundaries();
        commonMistakes();
    }

    // ========== 1. 创建与取值 ==========
    static void createAndUnwrap() {
        Optional<String> present = Optional.of("有值");           // of：确定非 null（传 null 直接 NPE）
        Optional<String> empty = Optional.empty();                 // empty：确定是空
        Optional<String> maybe = Optional.ofNullable(findNick()); // ofNullable：不确定时用

        // 取值的正确姿势（按推荐程度排序）
        // ① orElse：给个默认值（最常用）
        System.out.println("orElse: " + maybe.orElse("匿名"));

        // ② orElseGet：默认值"算"出来的时候用（延迟计算，有值时不执行）
        System.out.println("orElseGet: " + maybe.orElseGet(() -> expensiveDefault()));

        // ③ orElseThrow：没有就抛异常（比裸 get 好，至少意图明确）
        try {
            empty.orElseThrow(() -> new IllegalStateException("必须存在却没有"));
        } catch (IllegalStateException e) {
            System.out.println("orElseThrow: " + e.getMessage());
        }

        // ④ ifPresent / ifPresentOrElse：只想"有就做点啥"
        present.ifPresent(v -> System.out.println("ifPresent: 拿到 " + v));
        empty.ifPresentOrElse(
                v -> System.out.println("有: " + v),
                () -> System.out.println("ifPresentOrElse: 走了空的分支"));

        // 反面教材：get() 不做检查直接拿 —— 和裸引用一样会炸，毫无进步
        // empty.get();  // NoSuchElementException
    }

    static String findNick() {
        return null;   // 模拟"查不到"
    }

    static String expensiveDefault() {
        System.out.println("  （expensiveDefault 被调用了）");
        return "昂贵的默认值";
    }

    // 模拟嵌套结构：User -> Address(可能为 null) -> city
    record Address(String city) {
    }

    record User(String name, Address address) {
    }

    // ========== 2. 链式处理：map / filter / flatMap ==========
    static void chaining() {
        User withAddr = new User("小明", new Address("北京"));
        User noAddr = new User("小红", null);

        // 传统写法：层层判空，箭头代码
        // String city = null;
        // if (user != null && user.address() != null) { city = user.address().city(); }

        // Optional 链式：一层层"有就继续，没有就短路到空"
        System.out.println("\n有地址: " + cityOf(withAddr).orElse("未知"));
        System.out.println("无地址: " + cityOf(noAddr).orElse("未知"));

        // filter：盒子里的值不满足条件就变成空盒子
        Optional<String> longName = Optional.of("小明明明明")
                .filter(n -> n.length() > 2);
        Optional<String> shortName = Optional.of("小明")
                .filter(n -> n.length() > 2);
        System.out.println("filter 保留: " + longName + "，filter 滤空: " + shortName);
    }

    // 注意返回类型：Optional<String> —— 签名自己就会说话："可能没有"
    static Optional<String> cityOf(User user) {
        // map：lambda 返回普通值时用，Optional<User> -> Optional<Address> -> Optional<String>
        return Optional.ofNullable(user)
                .map(User::address)
                .map(Address::city);
        // flatMap：lambda 返回的本身就是 Optional 时用（把 Optional<Optional<X>> 拍平），
        // 例如 address() 的返回类型就是 Optional<Address> 时：.flatMap(User::address)
    }

    // ========== 3. 正确边界：只做返回值，不做字段和参数 ==========
    static void correctUsageBoundaries() {
        System.out.println("\n-- Optional 的使用边界 --");

        // ✅ 推荐：作为方法返回值，声明"可能没有结果"
        // Optional<User> findById(long id)

        // ❌ 不做实体类的字段：
        //   1. Optional 不是 Serializable，序列化框架会出问题
        //   2. 字段本该表达"对象的状态"，null 就是"没设置"，语义够用
        // class User { private Optional<String> nick; }   // 不要这样

        // ❌ 不做方法参数：
        //   调用方被迫把每个参数都包一层 Optional，啰嗦；
        //   而且参数传 null 进来依然 NPE，防了个寂寞
        // void greet(Optional<String> name)               // 不要这样

        // ❌ 不做集合元素：List<Optional<String>> 是设计失误的信号
        System.out.println("返回值 ✅ / 字段 ❌ / 参数 ❌ / 集合元素 ❌");
    }

    // ========== 4. 常见误用 ==========
    static void commonMistakes() {
        Optional<String> maybe = Optional.ofNullable(null);

        // 误用 ①：isPresent + get —— 脱了裤子放屁，等价于 if (x != null) x
        // if (maybe.isPresent()) { doSomething(maybe.get()); }
        // 改进：maybe.ifPresent(this::doSomething)

        // 误用 ②：orElse 里放昂贵操作 —— orElse 的参数是无条件求值的！
        String r1 = Optional.of("有值").orElse(expensiveDefault());  // 有值也会调用 expensiveDefault
        String r2 = Optional.of("有值").orElseGet(() -> expensiveDefault()); // 有值时不执行
        System.out.println("\norElse vs orElseGet: 上面 expensiveDefault 只被打印了一次（orElse 那次）");

        // 误用 ③：用 Optional 包集合 —— 空集合本身就是"没有"，直接返回空 List
        // Optional<List<User>> findAll()   // 不要这样；返回 Collections.emptyList() 更好

        System.out.println("误用小结: isPresent+get ❌ / orElse 放昂贵调用 ❌ / 包集合 ❌");
        System.out.println("r1=" + r1 + ", r2=" + r2);
    }
}
