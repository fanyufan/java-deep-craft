# java-deep-craft

A craftsman learning Java from bytes to distributed systems. Documenting the journey of going deep. 从字节码到分布式，记录 Java 深学之路。十年磨一剑，一行一修行。

## 这是什么

一个 Java 后端深度学习仓库：以 [ROADMAP.md](ROADMAP.md) 为总路线（约 60 周，8 个阶段，从语法筑基到分布式与架构），按天执行、按阶段造轮子，所有练习代码与笔记都入库。

- 总路线：[ROADMAP.md](ROADMAP.md)
- 当前进度：**阶段 0（筑基）进行中**，每日任务清单见 [stage-0-daily.md](stage-0-daily.md)

## 目录结构

```
├── ROADMAP.md          # 总学习路线（8 个阶段的目标、出口标准、小项目）
├── stage-0-daily.md    # 阶段 0 每日任务清单（Day 1-14）
└── stage-0/            # 阶段 0 练习代码
    ├── day01/          # 环境验证与第一个程序
    ├── day02/          # Git 与 Maven（hello-maven / hello-maven-plugin）
    ├── day03/          # String 高频考点（equals vs ==、intern、不可变）
    ├── day04/          # sealed class + instanceof 模式匹配
    ├── day05/          # 集合框架上手（不可变集合语义）
    ├── day06/          # 异常体系与 NIO.2 文件 IO（行数统计小项目雏形）
    ├── day07/          # 泛型（PECS、类型擦除）、注解、反射（极简 IoC）
    ├── day08/          # Lambda / Stream / Optional（Stream 重写行数统计）
    └── reference/      # 阶段 0 参考代码（写完才准对答案，含小项目骨架）
```

每个 `dayXX/` 目录自带 README，含运行方式、checklist 和要点笔记。

## 运行约定

练习以单文件 `.java` demo 为主，编译运行：

```powershell
javac -encoding UTF-8 XxxDemo.java   # Windows 中文环境必须指定源码编码（默认 GBK 会编译失败）
java -Dfile.encoding=UTF-8 XxxDemo   # 让 JVM 按 UTF-8 输出，避免控制台中文乱码
```

## 执行规则（摘要）

- 产出物当测试：不看材料直接做当天产出物，卡壳才回头补对应章节
- 参考代码（`stage-0/reference/`）只在**完全卡住或写完后**对答案，提前看 = 自测失效
- 断档周末兜底，阶段 0 达标后才进阶段 1

完整规则见 [stage-0-daily.md](stage-0-daily.md) 开头。
