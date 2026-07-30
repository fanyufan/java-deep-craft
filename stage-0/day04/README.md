# day04：sealed 体系中的 final / sealed / non-sealed

sealed 接口用 `permits` 列出允许的实现类后，**每个实现类必须显式三选一**，声明自己对"被继承"的态度，不允许暧昧状态。

## 三个修饰符

### 1. `final` —— 到此为止，不许再继承

```java
sealed interface Shape permits Circle, Rectangle {}

final class Circle implements Shape {}   // 继承链到此终结
```

实现类是叶子节点，谁都不能再 extends 它。

### 2. `sealed` —— 继续玩"受限继承"

```java
sealed class Rectangle implements Shape permits Square {}  // 只有我点名的人才能继承我

final class Square extends Rectangle {}
```

实现类自己也想控制"谁能继承我"，就把自己声明为 sealed，并用自己的 `permits` 列出下一代白名单。继承链可以延续，但每一层都在管控之下。

### 3. `non-sealed` —— 放弃管控，谁都能继承我

```java
non-sealed class Rectangle implements Shape {}   // 恢复成普通类，随便继承

class MyRectangle extends Rectangle {}           // 不受限制
```

注意：这是 Java 里**唯一带连字符的关键字**。作用是把封闭的继承体系重新打开一个口子——从它开始往下回到传统的"随便继承"模式。

## 对比表

| 修饰符 | 能否被继承 | 继承者是否受限 |
|---|---|---|
| `final` | ❌ 不能 | —— |
| `sealed` | ✅ 可以 | 必须在 `permits` 白名单里 |
| `non-sealed` | ✅ 可以 | 不受限，谁都可以 |

## 为什么要强制三选一

编译器的设计意图：一个类进了 sealed 体系，它对"开放性"的态度必须明说——要么关死（final）、要么继续管控（sealed）、要么明确放开（non-sealed）。读代码的人一眼就能看清整个继承体系：

```java
sealed interface Expr permits Lit, Add, Neg {}

final class Lit implements Expr {}              // 叶子
non-sealed class Add implements Expr {}         // 开放的口子，可任意扩展
sealed class Neg implements Expr permits Pos {} // 继续受限延伸
final class Pos extends Neg {}
```

## 典型搭档：pattern matching + switch（JDK 21）

因为编译器知道所有可能的实现，`switch` 可以做**穷尽性检查**——少写一个分支直接编译报错。这是 sealed 真正的价值：把"类型只有这几种"变成编译器能检查的约束。

## 示例代码：ShapeDemo.java

本目录下的 `ShapeDemo.java` 是完整可运行的演示，一个文件覆盖三种修饰符：

- `Circle` —— **final**，叶子节点
- `Rectangle` —— **sealed**，只允许 `Square` 继承（`Square` 自身是 final）
- `FreeShape` —— **non-sealed**，`MyDoodle` 不在 permits 名单里也能继承它

编译运行（Windows 中文环境需指定 UTF-8 编码）：

```bash
cd stage-0/day04
javac -encoding UTF-8 ShapeDemo.java
java ShapeDemo
```

输出：

```
圆形，半径 = 2.0
正方形，边长 = 3.0
矩形，3.0 x 4.0
自由形状：随手涂鸦
```

注：本机 JDK 17，示例用 `instanceof` 模式匹配（JDK 16+ 正式特性）；JDK 21+ 可改为 switch 模式匹配，享受编译器穷尽性检查。
