import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Day 7：泛型 —— 型变（? extends / ? super，PECS 原则）、类型擦除。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 GenericsDemo.java
 *   java -Dfile.encoding=UTF-8 GenericsDemo
 */
public class GenericsDemo {

    public static void main(String[] args) throws Exception {
        pecsProducerExtends();
        pecsConsumerSuper();
        pecsCombined();
        typeErasure();
    }

    // ========== 1. PECS：Producer Extends（生产者用 extends，只往外读） ==========
    static void pecsProducerExtends() {
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5);
        List<Number> numbers = List.of(10, 20);

        // sum 的参数是 ? extends Number：接受 Number 及其任意子类的 List
        System.out.println("sum(ints)    = " + sum(ints));
        System.out.println("sum(doubles) = " + sum(doubles));
        System.out.println("sum(numbers) = " + sum(numbers));

        // 但 ? extends 的列表是"只读"的：编译器只知道里面是 Number 的某个子类，
        // 不确定是哪一个，所以除了 null 什么都不让 add
        List<? extends Number> producer = ints;
        Number n = producer.get(0);          // 读：安全，一定能当 Number 用
        // producer.add(1);                  // 编译错误！放进去的可能是错误类型
        System.out.println("? extends 读出: " + n + "（add 是编译错误，只能读）");
    }

    static double sum(List<? extends Number> list) {
        double total = 0;
        for (Number n : list) {
            total += n.doubleValue();
        }
        return total;
    }

    // ========== 2. PECS：Consumer Super（消费者用 super，只往里写） ==========
    static void pecsConsumerSuper() {
        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        // fill 的参数是 ? super Integer：接受 Integer 及其任意父类的 List
        fill(numbers);
        fill(objects);

        System.out.println("\nfill(numbers) -> " + numbers);
        System.out.println("fill(objects) -> " + objects);

        // ? super 的列表是"只写"的：编译器知道元素是 Integer 的某个父类，
        // 往里放 Integer 一定安全；但读出来只能当 Object
        List<? super Integer> consumer = numbers;
        consumer.add(42);                    // 写：安全
        Object o = consumer.get(0);          // 读：只能拿到 Object
        // Integer i = consumer.get(0);      // 编译错误！不知道实际是哪种父类
        System.out.println("? super 写入 42，读出只能当 Object: " + o.getClass().getSimpleName());
    }

    static void fill(List<? super Integer> list) {
        for (int i = 1; i <= 3; i++) {
            list.add(i);
        }
    }

    // ========== 3. PECS 合体：Collections.copy 的经典签名 ==========
    // src 只被读（生产者 -> extends），dest 只被写（消费者 -> super）
    static <T> void copy(List<? super T> dest, List<? extends T> src) {
        for (T item : src) {
            dest.add(item);
        }
    }

    static void pecsCombined() {
        List<Integer> src = List.of(1, 2, 3);
        List<Number> dest = new ArrayList<>();
        copy(dest, src);                     // Integer 流进 Number 列表，安全
        System.out.println("\ncopy(Integer -> Number): " + dest);

        // 记忆法：PECS = Producer Extends, Consumer Super
        // 参数只用来"读出来" -> ? extends T；只用来"放进去" -> ? super T；又读又写 -> 裸 T
    }

    // ========== 4. 类型擦除：泛型只在编译期存在 ==========
    static List<String> names = new ArrayList<>();   // 字段上的泛型签名会保留在字节码里

    static void typeErasure() throws Exception {
        // ① 运行期 List<String> 和 List<Integer> 是同一个 Class，泛型参数被"擦掉"了
        Class<?> c1 = new ArrayList<String>().getClass();
        Class<?> c2 = new ArrayList<Integer>().getClass();
        System.out.println("\nArrayList<String>.class == ArrayList<Integer>.class ? " + (c1 == c2));

        // ② 所以运行期无法判断泛型类型：if (list instanceof List<String>) 是编译错误
        //    也因此泛型类里不能 new T()、不能 T.class —— 运行期根本没有 T 的信息

        // ③ 擦除的"补偿"：编译器自动插入强制转换。下面两行编译后字节码等价
        List<String> list = new ArrayList<>();
        list.add("hello");
        String s1 = list.get(0);                       // 源码
        String s2 = (String) list.get(0);              // 编译后实际做的事
        System.out.println("编译器自动插入强转: " + s1 + " / " + s2);

        // ④ 但声明处的泛型签名会留在字节码里，反射能读回来（Spring/Jackson 靠这个工作）
        Field field = GenericsDemo.class.getDeclaredField("names");
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        System.out.println("反射读回字段泛型签名: " + field.getName() + " -> " + type.getActualTypeArguments()[0]);

        // 结论：泛型 = 编译期的类型检查 + 编译器代劳的强转，运行期零成本（这是和 C++ 模板的本质区别）
    }
}
