import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Day 8 产出：把 Day 6 的 TxtLineCounter 用 Stream 重写一遍。
 *
 * 用法：
 *   javac -encoding UTF-8 StreamTxtLineCounter.java
 *   java -Dfile.encoding=UTF-8 StreamTxtLineCounter [目录]
 *
 * 对照 Day 6 命令式版本的变化：
 *   - 手动 for 循环累加 -> map/filter/collect 流水线
 *   - 汇总用 Collectors.summingLong，分组报表用 groupingBy
 *   - "找第一个行数最多的文件"用 max + Optional 表达"可能没有"
 */
public class StreamTxtLineCounter {

    record FileStat(Path file, long lines) {
    }

    public static void main(String[] args) {
        Path dir = args.length > 0 ? Path.of(args[0]) : Path.of("../day06/sample-data");

        if (!Files.isDirectory(dir)) {
            System.err.println("目录无效: " + dir.toAbsolutePath());
            System.exit(1);
        }

        List<FileStat> stats = collectStats(dir);
        printReport(dir, stats);
    }

    /** 递归找 .txt -> 逐文件数行，全程流水线 */
    static List<FileStat> collectStats(Path dir) {
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".txt"))
                    .sorted()
                    .map(StreamTxtLineCounter::toStat)   // Path -> FileStat
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("遍历目录失败: " + dir, e);
        }
    }

    static FileStat toStat(Path file) {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return new FileStat(file, lines.count());
        } catch (IOException e) {
            // 单个文件读失败：行数记 -1，报表里标记出来（比 Day 6 的"跳过"更直给）
            return new FileStat(file, -1);
        }
    }

    static void printReport(Path dir, List<FileStat> stats) {
        System.out.println("扫描目录: " + dir.toAbsolutePath());
        System.out.println("------------------------------------");

        // 明细：forEach 是终止操作， lambda 里做副作用（打印）
        stats.forEach(s -> System.out.printf("%-30s %6s%n",
                dir.relativize(s.file()),
                s.lines() >= 0 ? s.lines() + " 行" : "读取失败"));

        System.out.println("------------------------------------");

        // 汇总：summingLong 替代手动 total +=
        long total = stats.stream()
                .filter(s -> s.lines() >= 0)
                .mapToLong(FileStat::lines)
                .sum();
        System.out.printf("共 %d 个 .txt 文件，总计 %d 行%n",
                stats.stream().filter(s -> s.lines() >= 0).count(), total);

        // 行数最多的文件：max 返回 Optional —— "可能没有"体现在类型上
        Optional<FileStat> biggest = stats.stream()
                .filter(s -> s.lines() >= 0)
                .max((a, b) -> Long.compare(a.lines(), b.lines()));
        biggest.ifPresent(s -> System.out.println(
                "行数最多: " + dir.relativize(s.file()) + "（" + s.lines() + " 行）"));

        // 按行数规模分组报表：演示 groupingBy（数据少时价值不大，演示用法为主）
        Map<String, List<FileStat>> bySize = stats.stream()
                .filter(s -> s.lines() >= 0)
                .collect(Collectors.groupingBy(s -> s.lines() >= 5 ? "5 行及以上" : "5 行以下"));
        System.out.println("按规模分组: " + bySize.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue().size() + " 个")
                .collect(Collectors.joining("，")));
    }
}
