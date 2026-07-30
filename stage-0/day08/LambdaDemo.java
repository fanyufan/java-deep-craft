import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Day 8：函数式接口与 Lambda —— Function / Predicate / Consumer / Supplier。
 *
 * 函数式接口 = 只有一个抽象方法的接口，是 Lambda 的"类型载体"。
 * Lambda 本质：把一段行为（代码）当成值传来传去。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 LambdaDemo.java
 *   java -Dfile.encoding=UTF-8 LambdaDemo
 */
public class LambdaDemo {

    public static void main(String[] args) {
        fourCoreInterfaces();
        lambdaSyntaxShorthand();
        methodReference();
        combinePredicates();
        closureCapture();
    }

    // ========== 1. 四大核心函数式接口 ==========
    static void fourCoreInterfaces() {
        // Function<T, R>：有进有出，T -> R（转换）
        Function<String, Integer> length = s -> s.length();
        System.out.println("Function: \"hello\" 的长度 = " + length.apply("hello"));

        // Predicate<T>：有进，出 boolean，T -> boolean（判断/过滤）
        Predicate<String> isLong = s -> s.length() > 3;
        System.out.println("Predicate: \"hello\" 算长字符串? " + isLong.test("hello"));

        // Consumer<T>：只进不出，T -> void（消费/副作用）
        Consumer<String> printer = s -> System.out.println("Consumer: 打印 -> " + s);
        printer.accept("被消费掉了");

        // Supplier<T>：只出不进，() -> T（生产/延迟提供）
        Supplier<Double> random = () -> Math.random();
        System.out.println("Supplier: 生产一个随机数 = " + random.get());

        // 记忆法：Function 转换、Predicate 判断、Consumer 消费、Supplier 生产
        // 名字里带 Bi 的是两个入参：BiFunction<T,U,R>、BiConsumer<T,U>...
        //  UnaryOperator<T> = Function<T,T>（进出同类型），BinaryOperator<T> = BiFunction<T,T,T>
    }

    // ========== 2. Lambda 语法简写阶梯 ==========
    static void lambdaSyntaxShorthand() {
        // 同一个 Function 的四种写法，从完整到最简
        Function<String, Integer> f1 = (String s) -> { return s.length(); };  // 完整：类型+花括号+return
        Function<String, Integer> f2 = (s) -> s.length();                     // 类型可推断；单表达式可省花括号和 return
        Function<String, Integer> f3 = s -> s.length();                       // 单参数可省括号
        Function<String, Integer> f4 = String::length;                        // 方法引用（下一节）

        System.out.println("\n四种写法结果一致: "
                + f1.apply("abc") + f2.apply("abc") + f3.apply("abc") + f4.apply("abc"));

        // 多参数和无参的括号不能省
        // BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        // Runnable r = () -> System.out.println("run");
    }

    // ========== 3. 方法引用：Lambda 的进一步简写 ==========
    static void methodReference() {
        List<String> names = List.of("小明", "小红", "小刚");

        // 四种方法引用
        names.forEach(System.out::println);          // ① 静态方法? 不，这是 ② 的特例：特定对象的实例方法
        Function<String, Integer> f = String::length; // ③ 任意对象的实例方法（第一个参数当 this）
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(String::compareTo);              // 同上：a.compareTo(b)
        Supplier<List<String>> factory = ArrayList::new; // ④ 构造器引用

        // 静态方法引用
        Function<String, Integer> parse = Integer::parseInt;
        System.out.println("\n静态方法引用 parseInt: " + parse.apply("42"));
        System.out.println("构造器引用造出的 list: " + factory.get());

        // 判断标准：lambda 体只是"把参数原样转交给一个已有方法"时，就能换成方法引用
    }

    // ========== 4. 组合：函数式接口的默认方法 ==========
    static void combinePredicates() {
        Predicate<String> startsWithA = s -> s.startsWith("a");
        Predicate<String> longEnough = s -> s.length() > 3;

        // and / or / negate：像拼乐高一样拼条件
        List<String> words = List.of("apple", "app", "banana", "avocado");
        List<String> result = new ArrayList<>();
        for (String w : words) {
            if (startsWithA.and(longEnough).test(w)) {
                result.add(w);
            }
        }
        System.out.println("\na 开头且长度>3: " + result);
        System.out.println("非 a 开头: " + words.stream().filter(startsWithA.negate()).toList());

        // Function 的组合：andThen（先我后他）、compose（先他后我）
        Function<Integer, Integer> doubleIt = x -> x * 2;
        Function<Integer, Integer> plusTen = x -> x + 10;
        System.out.println("doubleIt.andThen(plusTen).apply(5) = " + doubleIt.andThen(plusTen).apply(5)); // 20
        System.out.println("doubleIt.compose(plusTen).apply(5) = " + doubleIt.compose(plusTen).apply(5)); // 30
    }

    // ========== 5. 闭包捕获：lambda 用外面的局部变量 ==========
    static void closureCapture() {
        String prefix = "捕获的外部变量";   // 必须是 final 或"事实 final"（赋值后没再改）
        Consumer<String> c = s -> System.out.println("\n" + s + ": " + prefix);
        c.accept("lambda 内");

        // prefix = "改一下";  // 如果放开这行，上面 lambda 编译报错：
        // "Variable used in lambda expression should be final or effectively final"

        // 为什么要求 final？lambda 捕获的是变量的"值拷贝"，如果原变量能改，
        // 拷贝和原值就会不一致，Java 干脆禁止，避免产生"以为能同步"的错觉

        // 对比：对象字段不受此限（捕获的是 this 引用，不是字段的值）
        Map<String, Integer> counter = new HashMap<>();
        List.of("a", "b", "a").forEach(s -> counter.merge(s, 1, Integer::sum));  // 合法
        System.out.println("用 map 当计数器: " + counter + "（绕过 final 限制的惯用法，但要意识到这是共享可变状态）");
    }
}
