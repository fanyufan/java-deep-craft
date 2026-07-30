import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Day 7：注解 —— 自定义注解，理解 @Target / @Retention。
 *
 * 注解本质是"贴在代码上的标签"，本身不做任何事；
 * 有人（编译器 / 框架 / 你的反射代码）去读它，它才有意义。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 AnnotationDemo.java
 *   java -Dfile.encoding=UTF-8 AnnotationDemo
 */
public class AnnotationDemo {

    // ========== 1. 定义一个注解 ==========
    // @Target：能贴在哪 —— TYPE(类)、FIELD(字段)、METHOD(方法)、PARAMETER(参数)...
    // @Retention：活多久 —— SOURCE(编译后丢弃) / CLASS(留在字节码但运行期读不到) / RUNTIME(运行期反射可读)
    // 想让反射读到，就必须是 RUNTIME
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Route {
        String path();                 // 注解的"属性"（无默认值，使用时必须填）
        String method() default "GET"; // 有默认值，可省略
    }

    // ========== 2. 使用注解 ==========
    static class UserController {
        @Route(path = "/users")                          // method 用默认值 GET
        public void list() {
        }

        @Route(path = "/users", method = "POST")
        public void create() {
        }

        public void notARoute() {
        }
    }

    // ========== 3. 读取注解：没有这一步，注解只是摆设 ==========
    public static void main(String[] args) {
        // 模拟框架做的事：扫描类里的方法，把 @Route 收集成路由表
        System.out.println("扫描 UserController 的路由表：");
        for (Method m : UserController.class.getDeclaredMethods()) {
            Route route = m.getAnnotation(Route.class);  // RUNTIME 才能这样读
            if (route != null) {
                System.out.printf("  %-4s %-8s -> %s()%n", route.method(), route.path(), m.getName());
            }
        }

        // isAnnotationPresent：只关心"有没有"，不关心内容
        try {
            Method m = UserController.class.getDeclaredMethod("notARoute");
            System.out.println("notARoute 有 @Route? " + m.isAnnotationPresent(Route.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // ========== 4. Retention 三种级别对比 ==========
        // SOURCE  ：如 @Override，编译期检查完就扔，字节码里都没有
        // CLASS   ：留在 .class 文件里，但 JVM 加载后读不到（默认值，反射拿不到！）
        // RUNTIME ：运行期反射可读，框架专用（@Autowired、@Test、@RequestMapping 都是）

        // 常见坑：自定义注解忘了加 @Retention(RUNTIME)，反射读出来永远是 null
    }
}
