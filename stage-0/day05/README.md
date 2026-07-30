# day05：集合框架上手（CollectionDemo.java）

只管会用，原理留给阶段 1。示例代码见本目录 `CollectionDemo.java`，已编译运行验证。

## 运行方式

```powershell
cd stage-0\day05
javac -encoding UTF-8 CollectionDemo.java
java CollectionDemo
```

若控制台输出中文乱码：运行时加 `-Dfile.encoding=UTF-8`（控制台是 GBK，让 JVM 按 UTF-8 输出），即 `java -Dfile.encoding=UTF-8 CollectionDemo`；或先执行 `chcp 65001` 把控制台切到 UTF-8。

## Checklist

- [x] `List` / `Set` / `Map` 常用实现类的使用场景
- [x] 遍历方式：`for-each`、迭代器、`forEach` + lambda
- [x] 可变 vs 不可变集合：`List.of()` / `Collections.unmodifiableList()`
- [x] 产出：小 demo，重点感受不可变集合的语义

## 1. 常用实现类的使用场景

选集合先问自己三个问题：要不要去重？要不要保序？要不要排序？

| 接口 | 实现类 | 语义 | 使用场景 |
|---|---|---|---|
| List | `ArrayList` | 有序、可重复、按下标查快 | **默认选择**，90% 的场景 |
| List | `LinkedList` | 链表，头尾增删快、按下标查慢 | 很少用；两头操作更多用 `ArrayDeque` |
| Set | `HashSet` | 去重、无序 | 只关心"有没有"，不关心顺序 |
| Set | `LinkedHashSet` | 去重 + 保持插入顺序 | 去重但要按添加顺序遍历 |
| Set | `TreeSet` | 去重 + 自动排序 | 去重且要求有序输出 |
| Map | `HashMap` | 键唯一、无序 | **默认选择**；键重复 put 会覆盖旧值 |
| Map | `LinkedHashMap` | 保持插入顺序 | 遍历顺序要可预期（如配置项） |
| Map | `TreeMap` | 按键自动排序 | 需要按 key 有序遍历 |

## 2. 三种遍历方式

- **for-each**：只读遍历首选，最常用
- **Iterator**：需要在遍历中删除元素时才用，`it.remove()` 是唯一安全的"边遍历边删"方式（直接 `list.remove()` 会抛 `ConcurrentModificationException`）
- **forEach + lambda**：函数式风格，配合 Stream 更强大；Map 可用 `map.forEach((k, v) -> ...)`

## 3. 不可变集合的语义（重点）

demo 中四个递进的实验结论：

1. **`List.of()` 创建即不可变**：`add` 编译能过、运行抛 `UnsupportedOperationException`；且不允许 null 元素（NPE）
2. **不可变只锁"结构"，不锁元素自身**：不可变 List 里装着 `StringBuilder`，元素内容照样能被改——不可变 ≠ 深度不可变
3. **`Collections.unmodifiableList()` 只是只读视图**：包装的是源列表的引用，源列表改了，视图跟着变
4. **`List.copyOf()` 才是真快照**：复制一份独立内容，源再变它不变

**实践约定**：方法返回值优先返回不可变集合（`List.of` / `List.copyOf`），防止调用方改坏内部状态；传入的集合不要原地改，要改自己 new 一份。
