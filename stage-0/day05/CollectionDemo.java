import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Day 5：集合框架上手 —— 会用，不究原理（原理留给阶段 1）。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 CollectionDemo.java
 *   java CollectionDemo
 */
public class CollectionDemo {

    public static void main(String[] args) {
        listScenes();
        setScenes();
        mapScenes();
        iteration();
        mutableVsImmutable();
    }

    // ========== 1. List：有序、可重复，按位置访问 ==========
    static void listScenes() {
        // ArrayList：默认选择。查快（按下标 O(1)），尾部增删快 —— 90% 的场景用它
        List<String> todo = new ArrayList<>();
        todo.add("学集合");
        todo.add("写 demo");
        todo.add("做笔记");
        System.out.println("ArrayList（有序可重复）: " + todo);
        System.out.println("按下标取第 2 个: " + todo.get(1));

        // LinkedList：链表，头尾增删快，按下标查慢。实际项目很少用，
        // 需要"两头操作"时更多用 ArrayDeque（当栈/队列用）
    }

    // ========== 2. Set：去重，不保证能按位置取 ==========
    static void setScenes() {
        // HashSet：去重，无序 —— "我只关心有没有，不关心顺序"时用它
        Set<String> tags = new HashSet<>();
        tags.add("java");
        tags.add("集合");
        tags.add("java");                       // 重复添加，无效
        System.out.println("\nHashSet（去重无序）: " + tags);

        // LinkedHashSet：去重 + 保持插入顺序
        Set<String> linked = new LinkedHashSet<>(List.of("b", "a", "c", "a"));
        System.out.println("LinkedHashSet（保持插入序）: " + linked);

        // TreeSet：去重 + 自动排序（元素要可比较）
        Set<String> sorted = new TreeSet<>(List.of("b", "a", "c", "a"));
        System.out.println("TreeSet（自动排序）: " + sorted);
    }

    // ========== 3. Map：键值对，键唯一 ==========
    static void mapScenes() {
        // HashMap：默认选择，键无序
        Map<String, Integer> scores = new HashMap<>();
        scores.put("小明", 90);
        scores.put("小红", 85);
        scores.put("小明", 95);                 // 键重复 → 覆盖旧值
        System.out.println("\nHashMap（键唯一）: " + scores);
        System.out.println("getOrDefault: " + scores.getOrDefault("小刚", 0));

        // LinkedHashMap：保持插入顺序
        Map<String, Integer> linked = new LinkedHashMap<>();
        linked.put("b", 1);
        linked.put("a", 2);
        linked.put("c", 3);
        System.out.println("LinkedHashMap（保持插入序）: " + linked);

        // TreeMap：按键自动排序
        Map<String, Integer> sorted = new TreeMap<>(linked);
        System.out.println("TreeMap（按键排序）: " + sorted);
    }

    // ========== 4. 三种遍历方式 ==========
    static void iteration() {
        List<String> langs = List.of("Java", "Kotlin", "Scala");

        // 方式一：for-each（最常用，只读遍历首选）
        System.out.println("\n-- for-each --");
        for (String lang : langs) {
            System.out.println(lang);
        }

        // 方式二：迭代器（遍历时需要删除元素才用它）
        System.out.println("-- Iterator（可边遍历边删）--");
        List<String> mutable = new ArrayList<>(langs);
        Iterator<String> it = mutable.iterator();
        while (it.hasNext()) {
            String lang = it.next();
            if (lang.startsWith("K")) {
                it.remove();                // 唯一安全的"遍历中删除"方式
            }
        }
        System.out.println("删除 K 开头后: " + mutable);

        // 方式三：forEach + lambda（函数式风格，配合 Stream 更强大）
        System.out.println("-- forEach + lambda --");
        langs.forEach(lang -> System.out.println("Hello, " + lang));

        // Map 的遍历：entrySet 一次拿到键和值
        Map<String, Integer> scores = Map.of("小明", 90, "小红", 85);
        scores.forEach((name, score) -> System.out.println(name + " = " + score));
    }

    // ========== 5. 可变 vs 不可变集合（重点感受语义） ==========
    static void mutableVsImmutable() {
        // --- List.of()：创建即不可变，改就抛异常 ---
        List<String> fixed = List.of("a", "b", "c");
        System.out.println("\nList.of 创建: " + fixed);
        try {
            fixed.add("d");                 // 编译能过，运行抛异常！
        } catch (UnsupportedOperationException e) {
            System.out.println("fixed.add(\"d\") -> 抛 UnsupportedOperationException");
        }
        // 注意：List.of 也不允许 null（List.of("a", null) 直接 NPE）

        // --- 不可变 ≠ 内容永远不能变：它只锁"结构"，不锁元素自身 ---
        List<StringBuilder> builders = List.of(new StringBuilder("x"));
        builders.get(0).append("-modified");    // 元素自身是可变对象，照样能改
        System.out.println("不可变 List 里的可变元素被改了: " + builders);

        // --- Collections.unmodifiableList()：只是"只读视图"，源列表变了它也变 ---
        List<String> source = new ArrayList<>(List.of("a", "b"));
        List<String> readOnly = Collections.unmodifiableList(source);
        source.add("c");                        // 改源列表
        System.out.println("unmodifiableList 是视图，源变它也变: " + readOnly);

        // --- 想要"真正不变的副本"：List.copyOf() ---
        List<String> snapshot = List.copyOf(source);
        source.add("d");
        System.out.println("copyOf 是快照，源再变它不变: " + snapshot);

        // --- 实践建议：方法返回值优先返回不可变集合，防止调用方改坏内部状态 ---
        System.out.println("团队约定: 返回值用 List.of/copyOf，入参别改，要改自己 new 一份");
    }
}
