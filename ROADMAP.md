# Java 学习路径：从入门到后端架构专家

> - JDK 版本：17（环境自行安装）
> - 投入：每周 10h+，总周期约 60 周（12-18 个月）
> - 材料原则：官方文档 / 源码为主，经典书籍为辅
> - 实践方式：主线项目贯穿全程 + 每阶段造轮子小项目
> - 源码深度：原理级阅读（讲清关键流程和设计意图，不逐行精读）

## 总览

| 阶段 | 主题 | 周期 | 小项目（造轮子） | 主线项目动作 |
|---|---|---|---|---|
| 0 | 筑基：语法与工具链 | W1-2 | 命令行文件整理工具 | — |
| 1 | Java 核心：集合 / 并发 / JVM 入门 | W3-8 | 手写 HashMap、手写线程池 | — |
| 2 | Java 攻坚：JVM 调优 / JUC 原理 | W9-14 | 压测调优实验、手写独占锁 | — |
| 3 | 工程化与数据库 | W15-20 | 手写 mini ORM | 技术选型与骨架 |
| 4 | Web 与 Spring 生态 | W21-28 | 手写 mini IoC 容器 | 单体版上线 |
| 5 | 分布式基础与中间件 | W29-38 | 手写 mini RPC | 拆服务、引入 MQ |
| 6 | 微服务：Spring Cloud Alibaba | W39-48 | 限流器 | 微服务化改造 |
| 7 | 系统设计与架构 | W49-60 | — | 最终形态 + 架构文档 |

主线项目建议：一个业务简单但可演化的系统（如**短链平台**或**简化版电商**），从单体一路演化到微服务，每个阶段把新学的技术栈叠加上去。

---

## 阶段 0：筑基（W1-2）

目标：补齐语法完整性，熟悉工具链。有工作基础，快速过。

- JDK 17 安装与 `JAVA_HOME` 配置、IDEA、Maven、Git
- 语法速通：OOP、集合、异常、IO、泛型、反射、注解
- Java 8-17 关键特性：Lambda / Stream / Optional、record、sealed class、switch 表达式、文本块、instanceof 模式匹配
- 资源：Oracle Java Tutorials；《Java 核心技术 卷 I》当字典查

**出口标准**：不看教程独立写出小项目；能说出 record/sealed 解决什么问题。

## 阶段 1：Java 核心（W3-8）

目标：日常开发最常用的三块——集合、并发、JVM 基础。

- 集合框架：`ArrayList` / `LinkedList` / `HashMap` / `ConcurrentHashMap` **原理级源码**（哈希、扩容、树化、CAS + synchronized）
- 并发基础：线程生命周期、`synchronized` / `volatile`、JUC 常用类、线程池参数与拒绝策略、`CompletableFuture`
- JVM 基础：运行时数据区、类加载过程与双亲委派、GC 基本概念
- IO / NIO / Netty 前置概念
- 资源：《Java 并发编程实战》；《深入理解 Java 虚拟机》第 1-3 章；JDK 源码

**小项目**：手写简化版 `HashMap`（哈希 + 扩容）；手写简化线程池（任务队列 + 工作线程 + 拒绝策略）。

**出口标准**：能画出 HashMap put 流程图；能讲清线程池从提交到执行的完整链路。

## 阶段 2：Java 攻坚（W9-14）

目标：从"会用"到"懂原理"，这是高级与中级开发的分水岭。

- JMM：happens-before、内存屏障、指令重排
- AQS 原理级源码：`ReentrantLock`、`Semaphore`、`CountDownLatch` 的设计共性
- GC 深入：分代假说、G1 / ZGC 原理、GC 日志分析
- 调优工具链：`jps` / `jstat` / `jmap` / `jstack`、Arthas、JFR
- 资源：《深入理解 Java 虚拟机》第 4-13 章；JEP 文档（G1: JEP 系列、ZGC: JEP 333）

**小项目**：压测调优实验（制造内存泄漏 / GC 频繁场景，用工具定位并调优）；基于 AQS 思想手写一个独占锁。

**出口标准**：给出一份 GC 日志能说出问题在哪；能讲清 AQS 的 CLH 队列与 park/unpark 机制。

## 阶段 3：工程化与数据库（W15-20）

目标：补齐工程规范，打下数据库深度地基。

- 工程化：Maven 依赖机制与生命周期、Git 分支模型、JUnit 5 / Mockito、SLF4J / Logback
- MySQL：InnoDB 索引（B+ 树、聚簇 / 二级索引、覆盖索引、最左前缀）、事务与 MVCC、锁（行锁 / 间隙锁 / 死锁）、`EXPLAIN` 与慢查询优化
- Redis：五种数据结构底层实现、持久化（RDB / AOF）、主从与哨兵、缓存穿透 / 击穿 / 雪崩
- 资源：MySQL 官方文档 + 《MySQL 是怎样运行的》；《Redis 设计与实现》

**小项目**：手写 mini ORM（JDBC + 反射 + 注解，实现简单的实体映射与 CRUD）。

**主线项目启动**：确定业务（短链 / 电商），定技术选型，搭工程骨架。

**出口标准**：能用 MVCC 解释一个并发读写场景的结果；能为给定 SQL 设计索引并说清理由。

## 阶段 4：Web 与 Spring 生态（W21-28）

目标：吃透 Spring 核心原理，主线项目单体版上线。

- HTTP 协议、Servlet 规范 → Spring MVC 请求处理链路
- Spring 核心：IoC 容器启动流程、Bean 生命周期、AOP 代理机制（原理级源码）
- Spring Boot：自动装配原理（`@Conditional`、`AutoConfiguration.imports`）、Starter 机制
- MyBatis：Mapper 代理原理、一级二级缓存
- 资源：Spring 官方文档（Core / Boot / MVC 章节质量很高）；源码配合调试

**小项目**：手写 mini IoC 容器（注解扫描 + 依赖注入 + 简单 AOP）。

**主线项目**：单体版上线（Spring Boot + MySQL + Redis + MyBatis），含单元测试与基本 CI。

**出口标准**：能画出 Spring 容器启动时序图；能手写一个自定义 Starter。

## 阶段 5：分布式基础与中间件（W29-38）

目标：建立分布式思维，掌握核心中间件原理。

- 分布式理论：CAP / BASE、一致性协议（Raft）、分布式事务（2PC / TCC / Saga / 本地消息表）
- Netty：Reactor 模型、ChannelPipeline、零拷贝
- RocketMQ：消息模型、存储原理、顺序 / 事务消息、重试与幂等
- 注册中心原理：Nacos（AP / CP 双模式）、ZooKeeper 对比
- Dubbo：作为 RPC 补充，理解 SPI、服务暴露 / 引用流程
- 资源：各中间件官方文档；《数据密集型应用系统设计》相关章节

**小项目**：手写 mini RPC（Netty 通信 + 序列化 + 注册中心 + 负载均衡），这是本路径含金量最高的轮子。

**主线项目**：按业务边界拆出 2-3 个服务，引入 RocketMQ 做异步解耦，处理缓存与 DB 一致性。

**出口标准**：能讲清 Raft 选主与日志复制；mini RPC 能跑通一次完整调用并讲清每层职责。

## 阶段 6：微服务 — Spring Cloud Alibaba（W39-48）

目标：掌握服务治理全家桶，主线项目完成微服务化。

- Nacos：服务注册发现 + 配置中心
- Sentinel：限流、熔断、降级的规则与滑动窗口原理
- Seata：AT / TCC 模式，落地分布式事务
- Gateway + OpenFeign：网关路由与声明式调用
- 链路追踪（Micrometer Tracing / SkyWalking）、Docker 化部署（顺带学）
- 资源：Spring Cloud Alibaba 官方文档与示例

**小项目**：手写滑动窗口 / 令牌桶限流器（对比 Sentinel 实现）。

**主线项目**：微服务化改造完成，具备限流熔断、配置中心、链路追踪能力。

**出口标准**：能为一个接口设计完整的"限流 + 熔断 + 降级 + 重试"保护链并说明参数依据。

## 阶段 7：系统设计与架构（W49-60）

目标：从"技术使用者"到"架构决策者"。

- 架构理论：DDD 基础（限界上下文、聚合）、整洁架构、事件驱动
- 高并发设计案例实战：秒杀系统、短链系统、Feed 流、排行榜——每个案例从容量评估 → 方案选型 → 落地
- 可观测性：Metrics / Tracing / Logging 三位一体
- 容量评估与全链路压测方法
- 技术决策能力：为每个方案写出 trade-off 分析
- 资源：《数据密集型应用系统设计》；《凤凰架构》（免费在线）；各大厂技术博客

**主线项目**：输出最终形态与完整架构文档（背景、容量估算、选型 trade-off、演进路线）。

**出口标准**：面对一个新业务场景，能独立产出容量评估、架构图与选型论证。

---

## 贯穿全程的习惯

- **源码笔记**：每次原理级阅读后画一张流程图，存入本仓库 `notes/` 目录
- **小项目入库**：每个轮子项目独立目录，README 写清"我学到了什么 / 与官方实现差在哪"
- **主线项目演进日志**：每次架构演进写一篇简短的 ADR（架构决策记录）
- **算法**：每周 2-3 道 LeetCode 保持手感即可，不占主时间

## 环境清单（自行安装）

- JDK 17（推荐 Temurin 17 最新补丁版）
- IDEA（Community 版够用，Ultimate 更佳）
- Maven 3.8+、Git
- MySQL 8、Redis 7（阶段 3 用到）
- Docker Desktop（阶段 6 用到）
