# day03：String 高频考点（StringDemo.java）

`StringDemo.java` 演示 String 的三个高频考点，已编译运行验证通过。

## 运行方式

```powershell
cd stage-0\day03
javac -encoding UTF-8 .\StringDemo.java
java StringDemo
```

- `javac -encoding UTF-8`：Windows 中文环境下 javac 默认按 GBK 读源码，文件是 UTF-8，必须显式指定编码
- `java StringDemo`：`java` 后跟**类名**，不带 `.\`、不带 `.class`

## 演示内容

### 1. equals vs ==

- `==` 比较引用，`equals` 比较内容
- 字面量进字符串常量池并复用：`"hello" == "hello"` 为 true
- `new String("hello")` 强制在堆上建新对象，`==` 为 false

### 2. 不可变性

- 所有"修改"方法（如 `concat`）都返回新对象，原对象不变；返回值被丢弃等于白调
- 循环拼接字符串要用 `StringBuilder`，循环里 `+=` 每次都新建 StringBuilder，性能差

### 3. 文本块（Java 15）

- `"""..."""` 多行字符串，告别满屏 `\n` 和转义

## 踩坑记录：java 命令的两种错误用法

### 错误一：把类名写成文件路径

```powershell
java .\StringDemo
# 错误: 找不到或无法加载主类 .\StringDemo
# 原因: java.lang.ClassNotFoundException: /\StringDemo
```

`java` 后只认**类名**不认文件路径，`.\StringDemo` 被当成全限定类名解析。

### 错误二：包声明与目录结构不匹配

类中声明了 `package com.deepcraft.stage0.day03;` 时，JVM 会按规则找：

```
classpath 根目录 + com\deepcraft\stage0\day03\StringDemo.class
```

而 `javac StringDemo.java` 默认把 class 输出在当前目录，路径对不上就报 `ClassNotFoundException`。
修复方式：编译时用 `-d` 让 javac 按包名建目录，并用 `-cp` 指定 classpath 根：

```powershell
javac -encoding UTF-8 -d out StringDemo.java
java -cp out com.deepcraft.stage0.day03.StringDemo
```

（本目录的 `StringDemo.java` 已去掉 package 声明，所以 `java StringDemo` 可直接运行。）

**规律**：`java` 命令只认"类名 + classpath"，永远不认文件路径。类名里的 `.` 会被当作目录分隔符去 classpath 下拼路径找 `.class`。这也是 Maven 的价值之一——`target/classes` 目录结构和 classpath 拼接都由它按规则自动完成。
