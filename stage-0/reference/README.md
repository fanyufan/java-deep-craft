# 阶段 0 第 1 周 · 参考代码

> ⚠️ **使用规则（与执行规则 3 对齐）**：先自己写，**完全卡住或写完后**才来这里对答案。
> 提前看 = 自测失效。

## 结构

| 包 | 对应天 | 主题 |
|---|---|---|
| `day1` | Day 1 | HelloWorld 与 JDK / JRE / JVM |
| `day3` | Day 3 | String 不可变性、文本块 |
| `day4` | Day 4 | sealed class + instanceof 模式匹配 |
| `day5` | Day 5 | 集合使用、不可变集合 |
| `day6` | Day 6 | NIO.2 文件统计（小项目雏形） |
| `day7` | Day 7 | 极简依赖注入（注解 + 反射） |

## 运行方式

IDEA 里直接跑各 `main` 类，或命令行：

```bash
mvn compile exec:java -Dexec.mainClass=com.deepcraft.stage0.day3.StringDemo
```

Day 6 需要传目录参数（默认当前目录）：

```bash
mvn compile exec:java -Dexec.mainClass=com.deepcraft.stage0.day6.FileStatsDemo -Dexec.args="some/dir"
```
