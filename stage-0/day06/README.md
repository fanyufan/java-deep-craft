# day06：异常与文件 IO（ExceptionDemo / FileIoDemo / TxtLineCounter）

只管会用，原理留给阶段 1。示例代码见本目录三个 `.java` 文件，均已编译运行验证。

## 运行方式

```powershell
cd stage-0\day06
javac -encoding UTF-8 ExceptionDemo.java FileIoDemo.java TxtLineCounter.java
java ExceptionDemo
java FileIoDemo
java TxtLineCounter              # 缺省统计 sample-data 目录
java TxtLineCounter 某目录        # 统计任意目录
```

若 Windows 控制台输出中文乱码：运行时加 `-Dfile.encoding=UTF-8` 即可（控制台是 GBK，这个参数让 JVM 按 UTF-8 输出），例如 `java -Dfile.encoding=UTF-8 TxtLineCounter`；也可以先执行 `chcp 65001` 把控制台切到 UTF-8。已验证：

```
C:\Fan\github\java-deep-craft\stage-0\day06> java -Dfile.encoding=UTF-8 TxtLineCounter
扫描目录: C:\Fan\github\java-deep-craft\stage-0\day06\sample-data
------------------------------------
notes.txt                           3 行
sub\diary.txt                       3 行
todo.txt                            4 行
------------------------------------
共 3 个 .txt 文件，总计 10 行
```

## Checklist

- [x] 异常体系：checked vs unchecked、try-with-resources、自定义异常 —— `ExceptionDemo.java`
- [x] 文件读写：`Files` / `Path`（NIO.2）——读、写、遍历目录 —— `FileIoDemo.java`
- [x] 产出：读取目录下所有 `.txt` 文件并统计行数 —— `TxtLineCounter.java`

## 1. 异常体系

### checked vs unchecked

| | checked（受检异常） | unchecked（非受检异常） |
|---|---|---|
| 继承自 | `Exception`（非 RuntimeException 分支） | `RuntimeException` |
| 编译器 | 强制处理，不 catch/throws 编译不过 | 不管 |
| 代表 | `IOException`、`SQLException` | `NullPointerException`、`ArrayIndexOutOfBoundsException` |
| 语义 | 外部世界不可靠（文件、网络） | 代码 bug，应该改代码而不是到处 try-catch |

记忆法：**checked 是"别人的错"（环境），unchecked 是"自己的错"（bug）**。

### try-with-resources

实现 `AutoCloseable` 的资源放进 `try (...)` 括号，出作用域自动 `close()`，正常结束和抛异常都关。demo 用打印验证了：**close 在 catch 之前执行**。凡是带 close 的资源（流、连接）一律用这个写法，别再写 finally 手动关。

### 自定义异常

- 想让调用方必须处理 → 继承 `Exception`（checked）；业务异常通常继承 `RuntimeException`（unchecked），避免方法签名被 throws 污染
- 抛异常时**带上上下文**（当前余额、尝试金额），排查问题省时间
- 包装异常时用 `super(message, cause)` **保留原始异常**，堆栈链不断

### 实践要点

1. 别吞异常：`catch (Exception e) {}` 是最差写法，bug 会被活埋
2. catch 顺序从具体到宽泛，子类在前（反了编译报错）
3. `finally` 总会执行，但关资源优先用 try-with-resources

## 2. 文件 IO：Files / Path（NIO.2）

别再用老的 `File` API：`File` 的方法失败只返回 `false` 不说原因，`Files` 失败抛带详细信息的 `IOException`。

### 常用操作速查

| 操作 | API |
|---|---|
| 拼路径 | `Path.of("a", "b.txt")`、`dir.resolve("c.txt")` |
| 判断 | `Files.exists(p)`、`Files.isDirectory(p)` |
| 写小文件 | `Files.writeString(p, text, UTF_8)`（追加加 `StandardOpenOption.APPEND`） |
| 读小文件 | `Files.readString(p, UTF_8)`、`Files.readAllLines(p, UTF_8)` |
| 大文件逐行 | `Files.lines(p, UTF_8)`（返回 Stream，**要 try-with-resources**） |
| 列一层 | `Files.list(dir)` |
| 递归遍历 | `Files.walk(dir)` |
| 复制/移动/删除 | `Files.copy` / `Files.move` / `Files.delete(IfExists)` |

两条铁律：

1. **永远显式指定编码**（`StandardCharsets.UTF_8`），别依赖平台默认编码
2. `Files.list` / `walk` / `lines` 返回的 Stream 持有文件句柄，**必须 try-with-resources 关闭**

## 3. 产出：TxtLineCounter（小项目雏形）

统计目录（含子目录）下所有 `.txt` 文件的行数，打印明细和总计。把当天的点全串起来了：

- 参数校验失败抛自定义 `InvalidDirectoryException`（unchecked，携带绝对路径和原因）
- `Files.walk` 递归 + 后缀过滤找 `.txt`，try-with-resources 关流
- `Files.lines` 逐行数，不把整个文件读进内存
- 单文件读失败记为失败项跳过，不拖垮整体；目录遍历不了则包装成 `UncheckedIOException` 带 cause 上抛
- 用 `record`（`FileStat` / `Report`）承载统计结果，main 只负责串流程

验证结果：`sample-data` 下 3 个 `.txt` 共 10 行，`readme.md` 被正确过滤；传不存在目录时打印明确错误并以 exit code 1 退出。
