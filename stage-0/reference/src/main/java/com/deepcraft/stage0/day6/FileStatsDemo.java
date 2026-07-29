package com.deepcraft.stage0.day6;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Day 6：文件统计 demo —— Day 12-13 小项目的雏形。
 *
 * 要点：
 *   - 用 NIO.2 的 Path / Files，不要再用老的 java.io.File
 *   - Files.walk 返回的 Stream 必须关闭（底层持有文件句柄），用 try-with-resources
 *   - Files.lines 同理
 */
public class FileStatsDemo {

    public static void main(String[] args) throws IOException {
        Path dir = args.length > 0 ? Path.of(args[0]) : Path.of(".");
        if (!Files.isDirectory(dir)) {
            System.err.println("目录不存在: " + dir.toAbsolutePath());
            System.exit(1);
        }

        long fileCount = 0;
        long totalLines = 0;

        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path p : paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .toList()) {
                long lines = countLines(p);
                System.out.println(p + " -> " + lines + " 行");
                fileCount++;
                totalLines += lines;
            }
        }

        System.out.println("---");
        System.out.println("共 " + fileCount + " 个 .txt 文件，" + totalLines + " 行");
    }

    private static long countLines(Path p) {
        try (Stream<String> lines = Files.lines(p)) {
            return lines.count();
        } catch (IOException e) {
            // Stream 管道里没法抛 checked 异常，包成 unchecked 是惯用手法
            throw new UncheckedIOException("读取失败: " + p, e);
        }
    }
}
