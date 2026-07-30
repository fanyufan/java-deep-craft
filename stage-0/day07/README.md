# day07：泛型、注解、反射（GenericsDemo / AnnotationDemo / ReflectionDemo / MiniIoC）

只管会用，原理留给阶段 1。示例代码见本目录四个 `.java` 文件，均已编译运行验证。

## 运行方式

```powershell
cd stage-0\day07
javac -encoding UTF-8 GenericsDemo.java AnnotationDemo.java ReflectionDemo.java MiniIoC.java
java -Dfile.encoding=UTF-8 GenericsDemo
java -Dfile.encoding=UTF-8 AnnotationDemo
java -Dfile.encoding=UTF-8 ReflectionDemo
java -Dfile.encoding=UTF-8 MiniIoC
```

`-Dfile.encoding=UTF-8`：Windows 控制台是 GBK，这个参数让 JVM 按 UTF-8 输出，避免中文乱码。

## Checklist

- [x] 泛型：型变（`? extends` / `? super`，PECS 原则）、类型擦除 —— `GenericsDemo.java`
- [x] 注解：自定义注解，`@Target` / `@Retention` —— `AnnotationDemo.java`
- [x] 反射：`Class` 对象、获取字段/方法并调用、性能代价 —— `ReflectionDemo.java`
- [x] 产出：极简依赖注入 demo（`@Inject` + 反射扫描赋值）—— `MiniIoC.java`

## 1. 泛型

### PECS 原则（Producer Extends, Consumer Super）

| 通配符 | 角色 | 能做什么 | 不能做什么 |
|---|---|---|---|
| `? extends T` | 生产者（只往外读） | 读出来当 `T` 用 | `add`（除 null 外编译错误） |
| `? super T` | 消费者（只往里写） | `add` 一个 `T` | 读出来只能当 `Object` |
| 裸 `T` | 又读又写 | 都行 | — |

记忆法：**参数只用来"读出来" → `? extends T`；只用来"放进去" → `? super T`**。经典例子是 `Collections.copy(dest, src)`：`src` 只读用 extends，`dest` 只写用 super。

### 类型擦除

demo 里四个递进的实验：

1. **运行期 `List<String>` 和 `List<Integer>` 是同一个 Class**——泛型参数被擦掉了，`instanceof List<String>`、`new T()`、`T.class` 都是编译错误
2. **擦除的补偿是编译器自动插入强转**：`String s = list.get(0)` 编译后实际是 `(String) list.get(0)`
3. **泛型 = 编译期的类型检查 + 编译器代劳的强转，运行期零成本**（这是和 C++ 模板的本质区别）
4. **但声明处的泛型签名留在字节码里**，反射 `getGenericType()` 能读回来——Spring/Jackson 靠这个工作

## 2. 注解

**注解本身不做任何事，有人去读它才有意义**（编译器 / 框架 / 你的反射代码）。

两个元注解：

- `@Target`：能贴在哪 —— `TYPE` / `FIELD` / `METHOD` / `PARAMETER`...
- `@Retention`：活多久 —— `SOURCE`（如 `@Override`，编译完就扔）/ `CLASS`（留在字节码，运行期读不到，**默认值**）/ `RUNTIME`（反射可读，框架专用）

**常见坑**：自定义注解忘了 `@Retention(RetentionPolicy.RUNTIME)`，反射 `getAnnotation()` 永远返回 null。

demo 定义了 `@Route(path, method)` 并模拟框架扫描方法生成路由表——`@RequestMapping` 的雏形。

## 3. 反射

### 核心 API

| 目的 | API |
|---|---|
| 拿 Class 对象 | `User.class` / `obj.getClass()` / `Class.forName("全限定名")`（每个类在 JVM 里只有一份，`==` 成立） |
| 读写字段 | `getDeclaredField(name)` + `setAccessible(true)` + `get/set(obj, val)` |
| 调方法 | `getMethod(name, 参数类型...)` + `invoke(obj, args...)`；私有方法用 `getDeclaredMethod` |
| 创建对象 | `getDeclaredConstructor(参数类型...).newInstance(args...)` |

`getXxx` vs `getDeclaredXxx`：前者只能拿 public（含继承的），后者拿本类声明的一切（含 private），配合 `setAccessible(true)` 关掉访问检查。

### 性能代价（实测）

1 亿次调用：直接调用 28 ms，反射调用（Method 已缓存）1609 ms，**约 56 倍**。慢在每次 `invoke` 的数组装包、访问检查，以及 JIT 难以内联。

实践结论：

- **Method/Field 对象要缓存复用**（查找比调用更贵），别在循环里 `getMethod`
- 框架在启动期反射建对象、运行期走缓存，所以业务代码感觉不到慢
- 热路径（每请求上万次调用）才需要关心，普通业务代码随便用

### 附：这个 56 倍是怎么测出来的（基准测试的坑）

初版 benchmark 测不出差距（反射反而"更快"），改了三处才得到可信数字，教训比数字本身更值得记住：**测性能时被测的东西必须占主导地位，且要防 JIT 把代码优化没**。

1. **被测方法必须"足够轻"**。初版用 `greet()`（内部做字符串拼接），拼接本身花几百纳秒，反射的调用开销只有十几纳秒——工作量淹没了调用开销，测出来的差异全是 JIT 噪声。换成 `getAge()`（只返回一个字段）后，测到的才几乎纯是"调用本身"的开销。
2. **结果必须被消费，防 JIT 消除**。`age += user.getAge()` 把结果累加起来；如果结果没人用，JIT 会把整个循环当死代码删掉，直接调用测出 0 ms，更假。
3. **先预热再计时**。先跑 100 万次让 JIT 把两种调用都编译成机器码，否则测的是"解释执行 vs 编译后执行"的不公平对比。

手写计时循环到处是这种坑，这是后面阶段要用 JMH（Java 官方基准测试框架）的原因。

## 4. 产出：MiniIoC（极简依赖注入，阶段 4 手写 IoC 的种子）

不到 50 行的容器：`getBean(Class)` 时用反射无参构造 new 对象 → 扫描字段找 `@Inject` → 递归创建依赖 → `field.set` 注入 → 单例缓存。演示了 `UserController -> UserService -> UserRepository` 三层自动装配，第二次 `getBean` 命中缓存。

踩到的一个真实坑：`Map.computeIfAbsent` 的映射函数里不允许递归修改 map（抛 `ConcurrentModificationException`），所以单例缓存要拆成"先查、没有再创建、put 回去"三步。

离 Spring 还差（阶段 4 再补）：按接口/按名注入（`@Qualifier`）、构造器注入、循环依赖检测、包扫描自动注册、生命周期回调。但核心思想已经全部在这个 demo 里：**反射 new 对象 + 反射读注解 + 反射写字段**。
