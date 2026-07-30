import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Day 8：Stream 常用操作 —— filter / map / collect / groupingBy / flatMap。
 *
 * Stream 三句话：
 *   ① 不是数据结构，是"流水线上的计算描述"，不存数据
 *   ② 中间操作（filter/map...）是惰性的，没有终止操作（collect/forEach...）一步都不执行
 *   ③ 只能用一次，消费完就废
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 StreamDemo.java
 *   java -Dfile.encoding=UTF-8 StreamDemo
 */
public class StreamDemo {

    record User(String name, String city, int age) {
    }

    static final List<User> USERS = List.of(
            new User("小明", "北京", 25),
            new User("小红", "上海", 30),
            new User("小刚", "北京", 17),
            new User("小丽", "上海", 22),
            new User("小华", "广州", 35),
            new User("小强", "北京", 28));

    public static void main(String[] args) {
        laziness();
        filterMapCollect();
        flatMap();
        groupingBy();
        sortingAndStats();
    }

    // ========== 0. 先感受惰性：没有终止操作，什么都不发生 ==========
    static void laziness() {
        System.out.println("-- 只组装流水线（注意：filter 里的打印不会执行）--");
        Stream<User> stream = USERS.stream()
                .filter(u -> {
                    System.out.println("  filter 检查: " + u.name());  // 没有终止操作，这行不会打印
                    return u.age() >= 18;
                });
        System.out.println("-- 流水线组装完毕，上面的 filter 一次都没跑 --");

        long count = stream.count();    // 终止操作一出现，整条流水线才真正执行
        System.out.println("-- count() 触发执行，成年人数量 = " + count + " --");
    }

    // ========== 1. filter / map / collect：最经典的三板斧 ==========
    static void filterMapCollect() {
        // 需求：找出成年的北京用户，只要名字，收集成 List
        List<String> names = USERS.stream()
                .filter(u -> u.age() >= 18)           // 留下成年人
                .filter(u -> u.city().equals("北京")) // 留下北京的
                .map(User::name)                      // User -> String（转换形状）
                .collect(Collectors.toList());        // 收网

        System.out.println("\n北京成年用户: " + names);

        // collect 的常用收集器
        String joined = USERS.stream().map(User::name).collect(Collectors.joining("、"));
        System.out.println("joining: " + joined);

        long adultCount = USERS.stream().filter(u -> u.age() >= 18).count();
        System.out.println("count 成年人数: " + adultCount);
    }

    // ========== 2. flatMap：把"流中流"拍平成一层 ==========
    static void flatMap() {
        // 每个用户的"城市拼音标签"有多个，想要所有用户的所有标签放在同一个 List 里
        record Order(String id, List<String> items) {
        }
        List<Order> orders = List.of(
                new Order("A1", List.of("手机", "贴膜")),
                new Order("A2", List.of("耳机")),
                new Order("A3", List.of("键盘", "鼠标", "显示器")));

        // map 会得到 List<List<String>>（三层套娃），flatMap 拍平
        List<String> allItems = orders.stream()
                .flatMap(o -> o.items().stream())     // List<String> -> Stream<String>，拍平合并
                .toList();                            // Java 16+ 的简洁收集（等价 collect(toList())）
        System.out.println("\nflatMap 所有订单商品: " + allItems);

        // 判断标准：lambda 返回的本身又是一个"集合/流"，而你想要平铺结果 -> flatMap
        // 经典场景：Optional 也能被 flatMap 拍平（stream().map(this::find).flatMap(Optional::stream)）
    }

    // ========== 3. groupingBy：SQL 的 GROUP BY ==========
    static void groupingBy() {
        // 按城市分组：Map<城市, List<User>>
        Map<String, List<User>> byCity = USERS.stream()
                .collect(Collectors.groupingBy(User::city));
        System.out.println("\n按城市分组: ");
        byCity.forEach((city, users) ->
                System.out.println("  " + city + ": " + users.stream().map(User::name).toList()));

        // 分组 + 下游统计：每个城市的人数
        Map<String, Long> countByCity = USERS.stream()
                .collect(Collectors.groupingBy(User::city, Collectors.counting()));
        System.out.println("每个城市人数: " + countByCity);

        // 分组 + 下游求平均：每个城市的平均年龄
        Map<String, Double> avgAgeByCity = USERS.stream()
                .collect(Collectors.groupingBy(User::city, Collectors.averagingInt(User::age)));
        System.out.println("每个城市平均年龄: " + avgAgeByCity);

        // partitioningBy：特殊的 groupingBy，key 固定 true/false（按条件二分）
        Map<Boolean, List<User>> adultOrNot = USERS.stream()
                .collect(Collectors.partitioningBy(u -> u.age() >= 18));
        System.out.println("成年人: " + adultOrNot.get(true).stream().map(User::name).toList());
        System.out.println("未成年: " + adultOrNot.get(false).stream().map(User::name).toList());
    }

    // ========== 4. 排序与统计 ==========
    static void sortingAndStats() {
        // 按年龄降序取前 3 名
        List<String> top3 = USERS.stream()
                .sorted(Comparator.comparingInt(User::age).reversed())
                .limit(3)
                .map(User::name)
                .toList();
        System.out.println("\n年龄最大的 3 人: " + top3);

        // 数值流：IntStream 避免装箱，还自带统计
        int totalAge = USERS.stream().mapToInt(User::age).sum();
        double avgAge = USERS.stream().mapToInt(User::age).average().orElse(0);
        int maxAge = USERS.stream().mapToInt(User::age).max().orElse(0);
        System.out.printf("总年龄 %d，平均 %.1f，最大 %d%n", totalAge, avgAge, maxAge);

        // anyMatch / allMatch / noneMatch：短路判断（找到答案就停）
        boolean hasMinor = USERS.stream().anyMatch(u -> u.age() < 18);
        boolean allChinese = USERS.stream().allMatch(u -> u.name().startsWith("小"));
        System.out.println("有未成年? " + hasMinor + "；都姓小? " + allChinese);
    }
}
