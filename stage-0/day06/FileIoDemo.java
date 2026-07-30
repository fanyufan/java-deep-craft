import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * Day 6：文件读写 —— Files / Path（NIO.2）。
 *
 * 别再用老的 File API：File 的方法失败时只返回 false 不告诉你原因，
 * Files 的方法失败会抛带详细信息的 IOException。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 FileIoDemo.java
 *   java FileIoDemo
 */
public class FileIoDemo {

    public static void main(String[] args) throws IOException {
        Path workspace = Path.of("io-demo-tmp");   // 本 demo 的试验目录
        Files.createDirectories(workspace);

        pathBasics();
        writeAndRead(workspace);
        walkDirectory(workspace);
        usefulOps(workspace);

        // 收尾：删掉试验目录
        try (Stream<Path> s = Files.walk(workspace)) {
            s.sorted((a, b) -> b.compareTo(a))     // 先删文件后删目录（深度优先倒序）
             .forEach(p -> {
                 try { Files.delete(p); } catch (IOException e) { /* 演示从简 */ }
             });
        }
        System.out.println("\n已清理试验目录 " + workspace);
    }

    // ========== 1. Path：路径的抽象 ==========
    static void pathBasics() {
        Path p = Path.of("docs", "notes", "a.txt");   // 跨平台，自动用对应分隔符
        System.out.println("路径: " + p);
        System.out.println("文件名: " + p.getFileName());
        System.out.println("父目录: " + p.getParent());
        System.out.println("绝对路径: " + p.toAbsolutePath());

        // resolve 拼接路径，normalize 消除 . 和 ..
        Path resolved = Path.of("docs").resolve("../docs/./b.txt").normalize();
        System.out.println("resolve+normalize: " + resolved);

        // 判断存在性、类型（注意：Files.exists，不是 path.exists）
        System.out.println("当前目录存在? " + Files.exists(Path.of(".")));
        System.out.println("是目录? " + Files.isDirectory(Path.of(".")));
    }

    // ========== 2. 读与写 ==========
    static void writeAndRead(Path dir) throws IOException {
        Path file = dir.resolve("hello.txt");

        // 写小文件：一次写完。显式指定 UTF-8，别依赖平台默认编码！
        Files.writeString(file, "第一行\n第二行\n第三行\n", StandardCharsets.UTF_8);
        System.out.println("\n写入: " + file);

        // 追加写（默认是覆盖）
        Files.writeString(file, "追加的一行\n", StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);

        // 读小文件：一次读成字符串
        String content = Files.readString(file, StandardCharsets.UTF_8);
        System.out.println("readString 读到 " + content.lines().count() + " 行");

        // 按行读成 List
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        System.out.println("readAllLines: " + lines);

        // 大文件：用 Stream 逐行处理，内存友好（流要关闭 -> try-with-resources）
        try (Stream<String> stream = Files.lines(file, StandardCharsets.UTF_8)) {
            long nonBlank = stream.filter(s -> !s.isBlank()).count();
            System.out.println("Files.lines 非空行数: " + nonBlank);
        }
    }

    // ========== 3. 遍历目录 ==========
    static void walkDirectory(Path dir) throws IOException {
        // 造一点目录结构
        Files.writeString(dir.resolve("a.txt"), "a\n", StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("b.md"), "# b\n", StandardCharsets.UTF_8);
        Path sub = dir.resolve("sub");
        Files.createDirectories(sub);
        Files.writeString(sub.resolve("c.txt"), "c\n", StandardCharsets.UTF_8);

        // Files.list：只列一层（不递归），返回 Stream，记得 try-with-resources
        System.out.println("\nFiles.list（只列一层）:");
        try (Stream<Path> s = Files.list(dir)) {
            s.forEach(p -> System.out.println("  " + p.getFileName()));
        }

        // Files.walk：递归遍历整个目录树
        System.out.println("Files.walk（递归）:");
        try (Stream<Path> s = Files.walk(dir)) {
            s.filter(Files::isRegularFile)
             .forEach(p -> System.out.println("  " + dir.relativize(p)));
        }

        // 找文件：walk + 过滤后缀
        System.out.println("所有 .txt 文件:");
        try (Stream<Path> s = Files.walk(dir)) {
            s.filter(p -> p.toString().endsWith(".txt"))
             .forEach(p -> System.out.println("  " + dir.relativize(p)));
        }
    }

    // ========== 4. 常用操作速查 ==========
    static void usefulOps(Path dir) throws IOException {
        Path src = dir.resolve("a.txt");
        Path copy = dir.resolve("a-backup.txt");

        Files.copy(src, copy);                                   // 复制
        System.out.println("\n复制: " + src.getFileName() + " -> " + copy.getFileName());

        Files.move(copy, dir.resolve("a-renamed.txt"));          // 移动/重命名
        System.out.println("重命名: a-backup.txt -> a-renamed.txt");

        System.out.println("大小: " + Files.size(src) + " 字节");

        Files.delete(dir.resolve("a-renamed.txt"));              // 删除（不存在会抛异常）
        Files.deleteIfExists(dir.resolve("不存在的.txt"));        // 不存在也不报错
        System.out.println("删除演示完成");
    }
}
