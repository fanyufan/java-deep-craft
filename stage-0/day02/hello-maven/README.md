# hello-maven：第一个 Maven 项目

用 Maven 官方骨架（archetype）生成的最小 Java 项目，主类 `com.deepcraft.App`，用于跑通 Maven 环境并理解坐标、生命周期与 jar 包。

## 创建步骤

### 1. 用 archetype 生成骨架

在 `stage-0/day02` 下执行：

```bash
cd stage-0/day02
mvn archetype:generate -DarchetypeArtifactId=maven-archetype-quickstart
```

交互式提示中填写：

```
Define value for property 'groupId': com.deepcraft
Define value for property 'artifactId': hello-maven
```

（version 默认 `1.0-SNAPSHOT`，package 默认与 groupId 相同，直接回车即可。）

也可以非交互一把生成：

```bash
mvn archetype:generate -DarchetypeArtifactId=maven-archetype-quickstart \
  -DgroupId=com.deepcraft -DartifactId=hello-maven -DinteractiveMode=false
```

生成的坐标：`com.deepcraft:hello-maven:1.0-SNAPSHOT`。

### 2. 项目结构

```
hello-maven/
├── pom.xml                              ← 项目唯一配置来源
└── src/
    ├── main/java/com/deepcraft/App.java       ← 主类（含 main 方法）
    └── test/java/com/deepcraft/AppTest.java   ← 测试类
```

## 运行方式

### 方式一：exec 插件直接运行（推荐，零配置）

```bash
cd stage-0/day02/hello-maven
mvn compile
mvn exec:java -Dexec.mainClass="com.deepcraft.App"
```

### 方式二：打包后手动运行

```bash
mvn package
java -cp target/hello-maven-1.0-SNAPSHOT.jar com.deepcraft.App
```

`java -cp <jar> <全限定类名>`：`-cp` 指定类路径（去哪找 .class），主类名是包名+类名，不是文件路径。

### 方式三：`java -jar`（需在 pom.xml 配置 Main-Class）

给 `maven-jar-plugin` 加配置：

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-jar-plugin</artifactId>
  <configuration>
    <archive>
      <manifest>
        <mainClass>com.deepcraft.App</mainClass>
      </manifest>
    </archive>
  </configuration>
</plugin>
```

然后：

```bash
mvn package
java -jar target/hello-maven-1.0-SNAPSHOT.jar
```

`java -jar` 从 jar 内 `META-INF/MANIFEST.MF` 的 `Main-Class` 读取入口。

## 常用命令速查

| 命令 | 作用 |
|---|---|
| `mvn compile` | 编译，产物在 `target/classes` |
| `mvn test` | 跑测试（surefire 插件） |
| `mvn package` | 打包 jar 到 `target/`（自动先编译、先测试） |
| `mvn install` | package 后装入本地仓库 `~/.m2/repository`，供本机其他项目依赖 |
| `mvn clean` | 删除 `target/` |
| `mvn help:effective-settings` | 打印合并后的 settings.xml，验证镜像等配置是否生效 |

## 概念备忘

- **坐标**：`groupId:artifactId:version` 全球唯一标识一个构件；groupId 是组织（域名倒写），artifactId 是项目名（全小写、连字符分词）
- **生命周期**：`validate → compile → test → package → verify → install → deploy`；执行某个 phase 会自动跑完它之前的所有 phase
- **插件**：Maven 核心是空壳，compile/test/package 等全由插件完成（`maven-compiler-plugin`、`maven-surefire-plugin`、`maven-jar-plugin`……）
