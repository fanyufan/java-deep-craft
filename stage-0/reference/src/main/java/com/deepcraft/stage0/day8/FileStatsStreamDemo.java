package com.deepcraft.stage0.day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Day 8：用 Stream 重写 Day 6 的文件统计 —— 同一个需求，感受声明式写法。
 * 额外加料：groupingBy 按扩展名统计文件数。
 */
public class FileStatsStreamDemo {

    public static void main(String[] args) throws IOException {
        Path dir = args.length > 0 ? Path.of(args[0]) : Path.of(".");

        // 按扩展名分组统计文件数
        try (Stream<Path> paths = Files.walk(dir)) {
            Map<String, Long> byExt = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.groupingBy(FileStatsStreamDemo::extension, Collectors.counting()));
            System.out.println("按扩展名统计 = " + byExt);
        }

        // .txt 总行数：mapToLong + sum，比 Day 6 的循环累加更声明式
        try (Stream<Path> paths = Files.walk(dir)) {
            long totalLines = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .mapToLong(FileStatsStreamDemo::countLinesQuietly)
                    .sum();
            System.out.println(".txt 总行数 = " + totalLines);
        }
    }

    private static String extension(Path p) {
        String name = p.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "(无扩展名)" : name.substring(dot + 1);
    }

    private static long countLinesQuietly(Path p) {
        try (Stream<String> lines = Files.lines(p)) {
            return lines.count();
        } catch (IOException e) {
            System.err.println("读取失败跳过: " + p);
            return 0;
        }
    }
}
