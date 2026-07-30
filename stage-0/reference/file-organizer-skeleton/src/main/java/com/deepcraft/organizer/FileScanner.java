package com.deepcraft.organizer;

import java.nio.file.Path;
import java.util.List;

/** 递归扫描目录下的常规文件 */
public class FileScanner {

    /**
     * @param root 扫描根目录
     * @return 所有常规文件（不含目录），不含已分类子目录中的文件
     * @throws IllegalArgumentException root 不存在或不是目录
     */
    public List<Path> scan(Path root) {
        // TODO: Files.walk + filter(Files::isRegularFile)。
        //       难点：root 下的分类目录（images/ docs/ ...）要排除，否则会把整理好的文件再搬一遍。
        throw new UnsupportedOperationException("待实现");
    }
}
