package com.deepcraft.organizer;

import java.nio.file.Path;

/** 把文件移动到 <root>/<category>/ 下；dryRun 模式只打印意图不动文件 */
public class FileMover {

    private final boolean dryRun;

    public FileMover(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * @return 目标路径（dryRun 时为"将要移动到的"路径）
     * 难点：
     *   1. 目标目录可能不存在 —— 先 Files.createDirectories。
     *   2. 同名文件冲突 —— 建议追加序号：a.jpg -> a-1.jpg、a-2.jpg……
     *   3. 文件本来就在正确的类别目录里 —— 应该跳过（返回原路径）。
     */
    public Path move(Path file, Path root, String category) {
        throw new UnsupportedOperationException("待实现");
    }
}
