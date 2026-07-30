import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Day 6 产出：统计目录下所有 .txt 文件的行数（含子目录）。
 *
 * 用法：
 *   javac -encoding UTF-8 TxtLineCounter.java
 *   java TxtLineCounter [目录]        （缺省统计当前目录下的 sample-data）
 *
 * 设计要点（小项目雏形）：
 *   - 参数校验失败抛自定义异常（unchecked，携带上下文）
 *   - Files.walk 递归找 .txt，try-with-resources 保证流关闭
 *   - 单个文件读失败不中断整体统计，记为失败项
 */
public class TxtLineCounter {

    /** 业务异常：目录不合法时抛出，调用方必须能看到明确原因 */
    static class InvalidDirectoryException extends RuntimeException {
        InvalidDirectoryException(Path dir, String reason) {
            super("目录无效: " + dir.toAbsolutePath() + "（" + reason + "）");
        }
    }

    /** 单个文件的统计结果 */
    record FileStat(Path file, long lines) {
    }

    public static void main(String[] args) {
        Path dir = args.length > 0 ? Path.of(args[0]) : Path.of("sample-data");

        try {
            Report report = countLines(dir);
            report.print();
        } catch (InvalidDirectoryException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /** 统计入口：校验目录 -> 收集 .txt -> 逐文件数行 */
    static Report countLines(Path dir) {
        if (!Files.exists(dir)) {
            throw new InvalidDirectoryException(dir, "不存在");
        }
        if (!Files.isDirectory(dir)) {
            throw new InvalidDirectoryException(dir, "不是目录");
        }

        List<FileStat> stats = new ArrayList<>();
        List<Path> failed = new ArrayList<>();

        // walk 返回的流持有目录句柄，必须 try-with-resources 关闭
        try (Stream<Path> stream = Files.walk(dir)) {
            List<Path> txtFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".txt"))
                    .sorted()
                    .toList();

            for (Path file : txtFiles) {
                try {
                    stats.add(new FileStat(file, countFileLines(file)));
                } catch (IOException e) {
                    // 一个文件失败（编码不对、权限不足等）不应拖垮整个统计
                    failed.add(file);
                }
            }
        } catch (IOException e) {
            // 目录本身就遍历不了：包装成 unchecked 往上抛，保留 cause
            throw new UncheckedIOException("遍历目录失败: " + dir, e);
        }

        return new Report(dir, stats, failed);
    }

    /** 数单个文件的行数：Files.lines 逐行流，不把整个文件读进内存 */
    static long countFileLines(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.count();
        }
    }

    /** 统计报告：明细 + 总计 + 失败清单 */
    record Report(Path dir, List<FileStat> stats, List<Path> failed) {
        void print() {
            System.out.println("扫描目录: " + dir.toAbsolutePath());
            System.out.println("------------------------------------");
            long total = 0;
            for (FileStat stat : stats) {
                System.out.printf("%-30s %6d 行%n", dir.relativize(stat.file()), stat.lines());
                total += stat.lines();
            }
            System.out.println("------------------------------------");
            System.out.printf("共 %d 个 .txt 文件，总计 %d 行%n", stats.size(), total);
            if (!failed.isEmpty()) {
                System.out.println("以下文件读取失败（已跳过）: " + failed);
            }
        }
    }
}
