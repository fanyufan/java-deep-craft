package com.deepcraft.organizer;

import java.util.Map;

/** 输出统计报告 */
public class StatsReporter {

    /**
     * 打印每个类别的文件数和总数，例如：
     *   images: 3
     *   docs:   2
     *   ----
     *   共 5 个文件
     */
    public void report(Map<String, Integer> stats) {
        // TODO: 用 Stream 或循环都行；总数可以用 values().stream().mapToInt 求和。
        throw new UnsupportedOperationException("待实现");
    }
}
