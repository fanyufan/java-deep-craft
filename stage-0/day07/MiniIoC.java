import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Day 7 产出：极简"依赖注入"容器 —— 自定义 @Inject，用反射扫描字段并赋值。
 * （阶段 4 手写 IoC 的种子，整个容器核心不到 50 行）
 *
 * 用法：
 *   javac -encoding UTF-8 MiniIoC.java
 *   java -Dfile.encoding=UTF-8 MiniIoC
 *
 * 设计要点：
 *   - @Inject 贴在字段上，RetentionPolicy.RUNTIME 才能被反射读到
 *   - 容器创建对象时扫描所有字段，遇到 @Inject 就递归创建依赖、set 进去
 *   - 单例缓存：同一个类只创建一次（UserService 和 UserController 共享同一个 Repository）
 */
public class MiniIoC {

    // ========== 注解：标记"这个字段需要容器来填" ==========
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Inject {
    }

    // ========== 容器核心 ==========
    static class Container {
        private final Map<Class<?>, Object> singletons = new HashMap<>();

        /** 取一个 Bean：没有就创建（并递归填好依赖），有就直接返回缓存的单例 */
        @SuppressWarnings("unchecked")
        <T> T getBean(Class<T> type) {
            Object cached = singletons.get(type);
            if (cached != null) {
                return (T) cached;
            }
            // 不能用 computeIfAbsent：创建过程中会递归 getBean 修改 map，
            // 而 computeIfAbsent 不允许在映射函数里改动 map（抛 ConcurrentModificationException）
            Object bean = createBean(type);
            singletons.put(type, bean);
            return (T) bean;
        }

        private Object createBean(Class<?> type) {
            try {
                // ① 无参构造 new 出对象 —— 反射创建实例
                Object bean = type.getDeclaredConstructor().newInstance();

                // ② 扫描所有字段，找贴了 @Inject 的 —— 反射读注解
                for (Field field : type.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Inject.class)) {
                        // ③ 递归解析依赖：依赖的依赖也会被自动填好
                        Object dependency = getBean(field.getType());
                        field.setAccessible(true);         // private 字段也要能写
                        field.set(bean, dependency);       // 反射赋值 —— "注入"发生在这里
                    }
                }
                return bean;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("创建 Bean 失败: " + type.getName(), e);
            }
        }
    }

    // ========== 演示用的三层结构：Controller -> Service -> Repository ==========
    static class UserRepository {
        String findName(long id) {
            return "用户#" + id + "（来自数据库）";
        }

        UserRepository() {
            System.out.println("  [容器] 创建 UserRepository @" + Integer.toHexString(hashCode()));
        }
    }

    static class UserService {
        @Inject
        private UserRepository repository;

        UserService() {
            System.out.println("  [容器] 创建 UserService");
        }

        String getUserName(long id) {
            return repository.findName(id);
        }
    }

    static class UserController {
        @Inject
        private UserService service;

        UserController() {
            System.out.println("  [容器] 创建 UserController");
        }

        String handleRequest(long id) {
            return service.getUserName(id);
        }
    }

    // ========== 跑起来 ==========
    public static void main(String[] args) {
        Container container = new Container();

        System.out.println("第一次 getBean(UserController)：");
        UserController controller = container.getBean(UserController.class);
        // 注意输出顺序：Controller 先被 new，然后容器发现 @Inject 字段，
        // 递归创建 Service -> Repository，再一层层填回去

        System.out.println("\n调用业务方法: " + controller.handleRequest(42));

        System.out.println("\n第二次 getBean(UserService)（应命中单例缓存，不再打印创建日志）：");
        UserService service = container.getBean(UserService.class);
        System.out.println("直接调用 Service: " + service.getUserName(7));

        // 验证单例：Controller 里的 Service 和直接取的是同一个对象
        System.out.println("\nController 里的 Service 和直接取的是同一个? 看上面的创建日志 —— UserService 只创建了一次");

        // 这个 demo 离 Spring 还差什么（阶段 4 再补）：
        //   - 按接口注入、按名字注入（@Qualifier）
        //   - 构造器注入、循环依赖检测
        //   - 包扫描自动注册（@ComponentScan），而不是手动 getBean
        //   - Bean 生命周期回调（@PostConstruct）
        // 但核心思想已经全部在这里了：反射 new 对象 + 反射读注解 + 反射写字段
    }
}
