# hello-maven-plugin：自定义 Maven 插件练手

一个最小可运行的自定义 Maven 插件，提供 `sayhi` goal，验证"Maven 所有实际工作都是插件完成的"这一模型。

## 创建步骤

### 1. 创建目录结构

```bash
cd stage-0/day02
mkdir -p hello-maven-plugin/src/main/java/com/deepcraft
cd hello-maven-plugin
```

### 2. 编写 `pom.xml`

关键点是 `<packaging>maven-plugin</packaging>`，以及两个 `provided` 依赖
（`maven-plugin-api`、`maven-plugin-annotations`）。

### 3. 编写 Mojo 类

`src/main/java/com/deepcraft/HelloMojo.java`：

- 继承 `AbstractMojo`，实现 `execute()` 方法
- `@Mojo(name = "sayhi")` 定义 goal 名
- `@Parameter(property = "name", defaultValue = "world")` 暴露可传参的参数

### 4. 安装到本地仓库

```bash
mvn install
```

`BUILD SUCCESS` 后，产物位于：

```
C:\Users\73405\.m2\repository\com\deepcraft\hello-maven-plugin\1.0-SNAPSHOT\
```

路径规律：`本地仓库根目录 + groupId(点变目录) + artifactId + version`。

### 5. 测试调用

换到**其他项目目录**（如 `hello-maven`）执行：

```bash
cd ../hello-maven
mvn com.deepcraft:hello-maven-plugin:1.0-SNAPSHOT:sayhi
mvn com.deepcraft:hello-maven-plugin:1.0-SNAPSHOT:sayhi -Dname=deepcraft
```

第二条输出 `Hello, deepcraft!`。

调用格式：`mvn <groupId>:<artifactId>:<version>:<goal名>`。

## 核心概念

- **Mojo** = Maven plain Old Java Object；一个插件可含多个 Mojo，每个 Mojo 即一个 **goal**（如 `exec:java` 中冒号后的部分）
- **生命周期 phase 是空槽位**，插件的 goal 绑定到 phase 上才真正干活
- 插件与普通依赖一样，是从仓库下载的构件，`mvn install` 后本机所有项目可用

## 常见坑

- 下载 `maven-plugin-api` 失败 → 网络/DNS 问题（检查阿里云镜像可达性）
- 第 5 步应在**别的项目目录**下执行，验证跨项目复用
- 进阶：`@Mojo(name = "sayhi", defaultPhase = LifecyclePhase.COMPILE)` 绑定到生命周期，配合引用方 `pom.xml` 中的 `<executions>`，`mvn compile` 时自动触发
