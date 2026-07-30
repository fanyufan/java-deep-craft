import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Day 7：反射 —— Class 对象、获取字段/方法并调用、性能代价。
 *
 * 反射 = 运行期"解剖"一个类：不看源码也能知道它有什么字段、什么方法，
 * 还能创建对象、读写私有字段、调用方法。框架的一切魔法都建在它上面。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 ReflectionDemo.java
 *   java -Dfile.encoding=UTF-8 ReflectionDemo
 */
public class ReflectionDemo {

    static class User {
        private String name = "默认值";
        private int age;

        public User() {
        }

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String greet() {
            return "你好，我是 " + name + "，" + age + " 岁";
        }

        public int getAge() {
            return age;
        }

        private String secret() {
            return "私有方法被调用了";
        }

        @Override
        public String toString() {
            return "User{name=" + name + ", age=" + age + "}";
        }
    }

    public static void main(String[] args) throws Exception {
        classObjectThreeWays();
        operateFields();
        invokeMethods();
        createInstances();
        performanceCost();
    }

    // ========== 1. 拿到 Class 对象的三种方式 ==========
    static void classObjectThreeWays() throws Exception {
        Class<?> c1 = User.class;                          // 类名字面量：编译期就确定
        Class<?> c2 = new User().getClass();               // 已有对象时
        Class<?> c3 = Class.forName("ReflectionDemo$User"); // 全限定名字符串：配置驱动，框架最爱

        System.out.println("三种方式拿到同一个 Class? " + (c1 == c2 && c2 == c3));
        // Class 对象是"类的元数据"，每个类在 JVM 里只有一份，所以 ==
    }

    // ========== 2. 字段：包括读写 private ==========
    static void operateFields() throws Exception {
        User user = new User("小明", 20);
        System.out.println("\n反射前: " + user);

        // getDeclaredField 能拿到 private 字段（getField 只能拿 public，含继承的）
        Field nameField = User.class.getDeclaredField("name");
        nameField.setAccessible(true);                     // 关掉 Java 访问检查，private 也能碰
        System.out.println("读 private 字段 name: " + nameField.get(user));
        nameField.set(user, "被反射改了");
        System.out.println("反射后: " + user);

        // 遍历所有字段（序列化、ORM 就是这么做的）
        System.out.print("所有字段: ");
        for (Field f : User.class.getDeclaredFields()) {
            System.out.print(f.getType().getSimpleName() + " " + f.getName() + "  ");
        }
        System.out.println();
    }

    // ========== 3. 方法：按名字+参数类型找到并调用 ==========
    static void invokeMethods() throws Exception {
        User user = new User("小红", 18);

        // public 方法：getMethod(方法名, 参数类型...)
        Method greet = User.class.getMethod("greet");
        Object result = greet.invoke(user);                // 返回值是 Object
        System.out.println("\ninvoke greet(): " + result);

        // private 方法：getDeclaredMethod + setAccessible
        Method secret = User.class.getDeclaredMethod("secret");
        secret.setAccessible(true);
        System.out.println("invoke 私有方法 secret(): " + secret.invoke(user));

        // 方法名从字符串来 —— 这就是"配置驱动"：写死的代码做不到
        String methodName = "greet";
        Method dynamic = User.class.getMethod(methodName);
        System.out.println("按字符串 \"" + methodName + "\" 动态调用: " + dynamic.invoke(user));
    }

    // ========== 4. 创建对象：构造器也能挑 ==========
    static void createInstances() throws Exception {
        // 无参构造（框架 new 对象的标准方式）
        User u1 = User.class.getDeclaredConstructor().newInstance();
        System.out.println("\n无参构造: " + u1);

        // 带参构造：按参数类型匹配
        Constructor<User> ctor = User.class.getDeclaredConstructor(String.class, int.class);
        User u2 = ctor.newInstance("小刚", 30);
        System.out.println("带参构造: " + u2);
    }

    // ========== 5. 性能代价：慢多少，为什么慢 ==========
    static int age;   // 黑洞：消费掉调用结果，防止 JIT 把整个循环优化掉

    static void performanceCost() throws Exception {
        User user = new User("测试", 1);
        int rounds = 100_000_000;

        // 预热，让 JIT 充分编译（不测预热会夸大差距）
        for (int i = 0; i < 1_000_000; i++) {
            age += user.getAge();
        }

        long t1 = System.nanoTime();
        for (int i = 0; i < rounds; i++) {
            age += user.getAge();                          // 直接调用
        }
        long direct = System.nanoTime() - t1;

        Method getAge = User.class.getMethod("getAge");
        long t2 = System.nanoTime();
        for (int i = 0; i < rounds; i++) {
            age += (int) getAge.invoke(user);              // 反射调用（Method 已缓存）
        }
        long reflective = System.nanoTime() - t2;

        System.out.printf("%n直接调用: %d ms%n", direct / 1_000_000);
        System.out.printf("反射调用: %d ms（约 %.1f 倍）%n", reflective / 1_000_000, (double) reflective / direct);

        // 为什么慢：① 每次 invoke 有数组装包、访问检查等开销 ② JIT 难以内联优化
        // 实践结论：
        //   - Method/Field 对象要缓存复用（查找比调用更贵），千万别在循环里 getMethod
        //   - 框架在启动期反射建对象、运行期走缓存，所以业务代码感觉不到慢
        //   - 热路径（每帧/每请求调用上万次）才需要关心，普通业务代码随便用
    }
}
