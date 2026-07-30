# day08：Lambda / Stream / Optional（LambdaDemo / StreamDemo / OptionalDemo / StreamTxtLineCounter）

示例代码见本目录四个 `.java` 文件，均已编译运行验证。

## 运行方式

```powershell
cd stage-0\day08
javac -encoding UTF-8 LambdaDemo.java StreamDemo.java OptionalDemo.java StreamTxtLineCounter.java
java -Dfile.encoding=UTF-8 LambdaDemo
java -Dfile.encoding=UTF-8 StreamDemo
java -Dfile.encoding=UTF-8 OptionalDemo
java -Dfile.encoding=UTF-8 StreamTxtLineCounter   # 缺省统计 ../day06/sample-data
```

`-Dfile.encoding=UTF-8`：Windows 控制台是 GBK，让 JVM 按 UTF-8 输出，避免中文乱码。

## Checklist

- [x] 函数式接口：`Function` / `Predicate` / `Consumer` / `Supplier` —— `LambdaDemo.java`
- [x] Stream 常用操作：filter / map / collect / groupingBy / flatMap —— `StreamDemo.java`
- [x] `Optional` 的正确用法（只做返回值，不做字段和参数）—— `OptionalDemo.java`
- [x] 产出：把 Day 6 的文件统计 demo 用 Stream 重写一遍 —— `StreamTxtLineCounter.java`

## 1. 函数式接口与 Lambda

**函数式接口 = 只有一个抽象方法的接口，是 Lambda 的类型载体**。四大核心：

| 接口 | 签名 | 角色 | 方法 |
|---|---|---|---|
| `Function<T,R>` | T -> R | 转换 | `apply` |
| `Predicate<T>` | T -> boolean | 判断/过滤 | `test` |
| `Consumer<T>` | T -> void | 消费/副作用 | `accept` |
| `Supplier<T>` | () -> T | 生产/延迟提供 | `get` |

衍生：`BiXxx` 两个入参；`UnaryOperator<T>` = `Function<T,T>`。

要点：

- **简写阶梯**：`(String s) -> { return s.length(); }` → `(s) -> s.length()` → `s -> s.length()` → `String::length`（方法引用）。方法引用的判断标准：lambda 体只是把参数原样转交给一个已有方法
- **组合**：`Predicate` 的 `and/or/negate`，`Function` 的 `andThen`（先我后他）/`compose`（先他后我）
- **闭包捕获**：lambda 用的局部变量必须 final 或事实 final——捕获的是值拷贝，允许改会造成"以为能同步"的错觉；`map.merge` 当计数器是绕过限制的惯用法，但那是共享可变状态

## 2. Stream

三句话：

1. Stream **不是数据结构**，是流水线上的计算描述，不存数据
2. 中间操作（filter/map...）**是惰性的**，没有终止操作（collect/count/forEach...）一步都不执行——demo 第一节用打印验证了这点
3. **只能用一次**，消费完就废

### 常用操作速查

| 操作 | 干什么 |
|---|---|
| `filter(p)` | 留下满足条件的 |
| `map(f)` | 一对一转换形状 |
| `flatMap(f)` | lambda 返回的本身是集合/流，要平铺结果时用（订单 -> 所有商品） |
| `sorted` / `limit` / `distinct` | 排序 / 截断 / 去重 |
| `collect(toList/joining/groupingBy/partitioningBy)` | 收网；groupingBy 就是 SQL 的 GROUP BY，可加下游 `counting/averagingInt` |
| `mapToInt` -> `sum/average/max` | 数值流避免装箱，自带统计 |
| `anyMatch/allMatch/noneMatch` | 短路判断，找到答案就停 |

## 3. Optional 的正确用法

定位：一个"最多装一个值"的盒子，让**"可能没有结果"体现在类型签名上**，逼调用方处理空的情况。

- **创建**：`of`（确定非 null）/ `empty` / `ofNullable`（不确定时用）
- **取值**：`orElse`（默认值）/ `orElseGet`（默认值要"算"时用，延迟执行）/ `orElseThrow` / `ifPresent(OrElse)`；裸 `get()` 是反面教材
- **链式**：`map`（返回普通值）/ `flatMap`（返回的本身是 Optional，防套娃）/ `filter`（不满足变空盒）——替代层层判空的箭头代码

**使用边界：只做返回值，不做字段、不做参数、不做集合元素。**

常见误用：

- `isPresent() + get()` —— 等价于 `if (x != null) x`，毫无进步，该用 `ifPresent`
- `orElse` 里放昂贵调用 —— orElse 的参数**无条件求值**，有值也会执行（demo 用打印验证）；该用 `orElseGet`
- 用 Optional 包集合 —— 空集合本身就是"没有"，返回空 List 即可

## 4. 产出：StreamTxtLineCounter（Day 6 统计的 Stream 版）

对照 Day 6 命令式版本的变化：

| Day 6（命令式） | Day 8（Stream） |
|---|---|
| for 循环逐文件统计 | `walk -> filter -> map(toStat) -> toList()` 流水线 |
| 手动 `total +=` 累加 | `mapToLong(FileStat::lines).sum()` |
| 失败清单单独记录 | 行数记 -1，报表里直接标记（更直给） |
| —（没有的功能） | `max` + `Optional` 找行数最多的文件、"可能没有"体现在类型上 |
| —（没有的功能） | `groupingBy` 按行数规模分组报表 |

验证结果：缺省扫描 `../day06/sample-data`，3 个 `.txt` 共 10 行，行数最多 `todo.txt`（4 行），与 Day 6 一致。
