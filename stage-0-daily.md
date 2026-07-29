# 阶段 0：筑基 — 每日任务清单（W1-2）

> 目标：补齐语法完整性，熟悉工具链，为后续阶段打好地基。
> 节奏：工作日每天 1.5-2h，周末每天约 4h。
> 前置：JDK 17 已安装 ✅
> 出口标准：不看教程独立写出小项目；能说出 record / sealed 解决什么问题。

## 执行规则（拷问确认版）

1. **学习时段**：工作日晚上下班后。加班/应酬断档是常态风险，按规则 2 处理。
2. **断档策略——周末兜底**：工作日缺的任务顺延到周末，周末从 4h 扩到 5-6h 消化；Day 14 机动日是最后防线。两周内最多容忍 3 个工作日断档。
3. **产出物当测试**：Day 3-6 这类工作中常用的内容，**不看材料直接做当天产出物**——顺利写完 → 阅读部分跳过；卡壳 → 回头只补卡壳对应的章节。禁止"先把材料过一遍再动手"。
4. **出口标准不达标**：用周末兜底时间补齐，阶段 0 最多延长 3-4 天，**达标后才进阶段 1**。带洞前进会在阶段 1 的集合源码和并发上付出代价。

---

## 第 1 周

### Day 1（工作日，1.5h）— 环境验证与第一个程序

- [ ] 验证环境：`java -version`、`javac -version` 确认是 17.x；确认 `JAVA_HOME` 指向正确
- [ ] 不用 IDE，手写 `HelloWorld.java`，用 `javac` 编译、`java` 运行，再试 `java HelloWorld.java`（JDK 11+ 单文件源码直接运行）
- [ ] 安装 IDEA（Community 即可），新建项目跑通同一个 HelloWorld
- [ ] 搞懂三个概念的关系：JDK / JRE / JVM（能用自己的话讲出来）
- [ ] 产出：仓库新建 `stage-0/` 目录，提交第一个 Java 文件

### Day 2（工作日，1.5h）— Git 与 Maven

- [ ] 配置 Git（`user.name` / `user.email`），熟悉 add / commit / log / diff 基本操作
- [ ] 安装 Maven 3.8+，配置 `settings.xml` 国内镜像（阿里云）
- [ ] 用 `mvn archetype:generate` 生成一个项目骨架，理解 `pom.xml` 基本结构（groupId / artifactId / dependencies）
- [ ] 跑通 `mvn compile` / `mvn package`，知道产物在哪
- [ ] 产出：理解 IDEA 项目、手动 javac、Maven 三者的关系

### Day 3（工作日，2h）— 基本语法速通

- [ ] 过一遍：基本类型 vs 引用类型、自动装箱、运算符、控制流、数组
- [ ] 重点抠 `String`：不可变性、`StringBuilder` / `StringBuffer` 区别、`equals` vs `==`
- [ ] 文本块（Java 15）：写一个多行 SQL / JSON 字符串试试
- [ ] 产出：把不熟悉点记到 `notes/stage-0.md`，熟悉的直接跳过

### Day 4（工作日，2h）— OOP 核心

- [ ] 类、继承、多态、抽象类、接口速过
- [ ] 重点：`final` 三个用法（类 / 方法 / 变量）、`static` 语义、方法重载 vs 重写
- [ ] **sealed class（Java 17）**：写一个 `sealed interface Shape permits Circle, Rect`，配合 pattern matching 使用
- [ ] 产出：能一句话说清 sealed 解决的问题（受控的继承层次，让编译器做穷尽检查）

### Day 5（工作日，2h）— 集合使用

- [ ] `List` / `Set` / `Map` 常用实现类的使用场景（原理留给阶段 1，这里只管会用）
- [ ] 遍历方式：`for-each`、迭代器、`forEach` + lambda
- [ ] 可变 vs 不可变集合：`List.of()` / `Collections.unmodifiableList()`
- [ ] 产出：写几个小 demo，重点感受不可变集合的语义

### Day 6（工作日，2h）— 异常与文件 IO

- [ ] 异常体系：checked vs unchecked、try-with-resources、自定义异常
- [ ] 文件读写：`Files` / `Path`（NIO.2，别再用老的 `File` API）——读、写、遍历目录
- [ ] 产出：写一个读取目录下所有 `.txt` 文件并统计行数的 demo（小项目的雏形）

### Day 7（周末，4h）— 泛型、注解、反射

- [ ] 泛型：型变（`? extends` / `? super`，PECS 原则）、类型擦除的概念
- [ ] 注解：自定义一个注解，理解 `@Target` / `@Retention`
- [ ] 反射：`Class` 对象、获取字段 / 方法并调用，知道反射的性能代价
- [ ] 产出：写一个极简"依赖注入" demo——自定义 `@Inject` 注解，用反射扫描并赋值（阶段 4 手写 IoC 的种子）

---

## 第 2 周

### Day 8（工作日，2h）— Lambda / Stream / Optional

- [ ] 函数式接口：`Function` / `Predicate` / `Consumer` / `Supplier`
- [ ] Stream 常用操作：filter / map / collect / groupingBy / flatMap
- [ ] `Optional` 的正确用法（只做返回值，不做字段和参数）
- [ ] 产出：把 Day 6 的文件统计 demo 用 Stream 重写一遍

### Day 9（工作日，2h）— Java 9-17 新特性收尾

- [ ] `var` 局部变量类型推断：哪里该用、哪里不该用
- [ ] `record`：写一个 `record Point(int x, int y)`，理解它自动生成了什么
- [ ] switch 表达式与 `yield`、instanceof 模式匹配
- [ ] 组合练习：`sealed` + `record` + switch 模式匹配写一个小型表达式求值器（这是 Java 17 的标志性写法）
- [ ] 产出：能回答出口标准第二问——record / sealed 各自解决什么问题

### Day 10（工作日，1.5h）— Maven 深入一点

- [ ] 依赖范围（scope）、依赖传递与冲突、`<dependencyManagement>` 的作用
- [ ] Maven 生命周期：clean / compile / test / package / install 各阶段干什么
- [ ] 产出：给小项目配上 pom，能用 `mvn package` 打出可运行 jar

### Day 11（工作日，1.5h）— JUnit 5 快速上手

- [ ] `@Test` / 断言 / `@BeforeEach` / 参数化测试（`@ParameterizedTest`）
- [ ] 产出：给已有的文件统计 demo 补上 3-5 个单测，`mvn test` 全绿

### Day 12（周末，4h）— 小项目：命令行文件整理工具（上）

需求：扫描指定目录，按文件扩展名分类移动到子文件夹（`images/`、`docs/`、`code/`…），支持 `--dry-run` 预览模式，输出统计报告。

- [ ] 设计：拆出类（Scanner / Classifier / Mover / Reporter），先写 README 描述设计
- [ ] 实现扫描与分类逻辑

### Day 13（周末，4h）— 小项目（下）

- [ ] 完成移动逻辑与统计报告，支持 `--dry-run`
- [ ] 补上单测，处理异常路径（目录不存在、权限不足、重名文件）
- [ ] 用 `mvn package` 打包，命令行运行验证
- [ ] 约束：至少用到 `record`、文本块、Stream、switch 表达式各一处

### Day 14（机动 / 复盘，1-2h）

- [ ] 对照出口标准自测：
  - 小项目是否独立写完、没抄教程？
  - 能否讲清 record / sealed 解决什么问题？
  - JDK / JRE / JVM、Maven 生命周期能否讲给别人听？
- [ ] 补 `notes/stage-0.md`：记录卡壳点和遗留问题
- [ ] 不达标项列出来，用机动时间补齐后进入阶段 1

---

## 备注

- Day 3-6 按执行规则 3 走"产出物当测试"：先动手，卡了才补材料。省下的时间归入周末兜底池，用于消化断档或提前启动 Day 12 的小项目。
- 卡壳优先查 [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/) 和 [JDK 17 API 文档](https://docs.oracle.com/en/java/javase/17/docs/api/)，养成读官方文档的习惯（本路径的材料原则）。
- 每天结束 `git commit`，小步提交。
