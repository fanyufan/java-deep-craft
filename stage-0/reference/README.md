# 阶段 0 参考代码（第 1-2 周）

> ⚠️ **使用规则（与执行规则 3 对齐）**：先自己写，**完全卡住或写完后**才来这里对答案。
> 提前看 = 自测失效。

## ⚠️ 预览特性说明

Day 9 用到 switch 模式匹配（JDK 17 预览特性），本模块已开启 `--enable-preview`，
因此**本模块所有类的编译和运行都需要该参数**：

- IDEA：Settings → Project Structure → Project language level 设为 `17 (Preview)`
- 命令行：`mvn compile` 后 `java --enable-preview -cp target/classes com.deepcraft.stage0.day9.ExprEvaluator`
- 测试：`mvn test`（surefire 已配好 `--enable-preview`）

## 结构

| 包 / 目录 | 对应天 | 主题 |
|---|---|---|
| `day1` | Day 1 | HelloWorld 与 JDK / JRE / JVM |
| `day3` | Day 3 | String 不可变性、文本块 |
| `day4` | Day 4 | sealed class + instanceof 模式匹配 |
| `day5` | Day 5 | 集合使用、不可变集合 |
| `day6` | Day 6 | NIO.2 文件统计（小项目雏形） |
| `day7` | Day 7 | 极简依赖注入（注解 + 反射） |
| `day8` | Day 8 | 函数式接口、Stream、Optional；Stream 重写文件统计 |
| `day9` | Day 9 | var / record / switch 表达式；sealed+record+模式匹配求值器 |
| `day11` | Day 11 | JUnit 5 示例（`src/test` 下） |
| `file-organizer-skeleton/` | Day 12-13 | 小项目骨架 + 设计提示（**故意不给实现**） |

## 运行方式

IDEA 里直接跑各 `main` 类；Day 6 / Day 8 的文件统计可传目录参数（默认当前目录）。
Day 11 运行 `mvn test`。

## file-organizer-skeleton 说明

小项目是阶段 0 的**出口标准**，所以这里只有接口骨架、TODO 注释和设计提示
（见该目录 README），没有实现。卡住时先看提示，还卡再对照骨架的类划分。
