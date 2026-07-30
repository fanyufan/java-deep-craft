package com.deepcraft.organizer;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * 入口：解析参数，组装流水线。
 *
 * 用法: java -jar file-organizer.jar <目录> [--dry-run]
 */
public final class Main {

    public static void main(String[] args) {
        // TODO: 1. 解析 args —— 第一个参数是目录，--dry-run 是开关。
        //           目录不存在时打印用法并以非零码退出。
        // TODO: 2. 构造分类规则表（扩展名 -> 类别），注入 FileClassifier。
        // TODO: 3. 组装流水线：scan -> 逐文件 classify -> move -> 收集结果 -> report。
        throw new UnsupportedOperationException("待实现");
    }

    /** 建议的分类规则，可按需调整 */
    static Map<String, String> defaultRules() {
        return Map.ofEntries(
                Map.entry("jpg", "images"), Map.entry("png", "images"), Map.entry("gif", "images"),
                Map.entry("txt", "docs"), Map.entry("md", "docs"), Map.entry("pdf", "docs"),
                Map.entry("java", "code"), Map.entry("py", "code"), Map.entry("js", "code")
        );
    }

    /** 扫描结果 -> 每个文件执行分类和移动，返回 类别 -> 成功移动数 */
    static Map<String, Integer> organize(Path root, boolean dryRun) {
        // TODO: 流水线主体。建议拆小方法，别在 main 里写全部逻辑。
        throw new UnsupportedOperationException("待实现");
    }
}
