package com.deepcraft.organizer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * 测试骨架：去掉 @Disabled，实现断言。
 * @TempDir 是 JUnit 5 自带的临时目录，每个测试方法独立，跑完自动清理。
 */
class FileOrganizerTest {

    @TempDir
    Path tempDir;

    @Test
    @Disabled("待实现")
    void classifier按扩展名分类() {
        // 提示：FileClassifier 是纯逻辑，直接 new 出来测，
        // 用 @ParameterizedTest + @CsvSource 覆盖 jpg/JPG/无扩展名/未收录扩展名。
    }

    @Test
    @Disabled("待实现")
    void scanner找到所有常规文件() {
        // 提示：在 tempDir 里造几个文件和子目录（Files.write / createDirectory），
        // 断言 scan 结果的数量和内容。
    }

    @Test
    @Disabled("待实现")
    void mover处理重名冲突() {
        // 提示：先在目标类别目录放一个 a.jpg，再移动另一个 a.jpg，断言得到 a-1.jpg。
    }

    @Test
    @Disabled("待实现")
    void dryRun不改动文件系统() {
        // 提示：dryRun 模式下调用 move，断言原文件还在、目标文件不存在。
    }
}
