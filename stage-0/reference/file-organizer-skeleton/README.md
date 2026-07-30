# 文件整理工具 · 骨架与设计提示

> ⚠️ 先读 `ROADMAP.md` / `stage-0-daily.md` 的需求，自己设计并动手。
> 只有卡住或写完之后，才回来对照这份提示。

## 需求回顾（Day 12-13）

扫描指定目录，按扩展名分类移动到子文件夹（`images/`、`docs/`、`code/`…），
支持 `--dry-run` 预览，输出统计报告。打包成可运行 jar。

## 建议的数据流

```
目录 → FileScanner → List<Path>
                    ↓ 每个文件
              FileClassifier → 类别名（如 "images"）
                    ↓
              FileMover(dryRun?) → 移动到 <目录>/<类别>/<文件名>
                    ↓ 移动结果
              StatsReporter → 统计报告
```

单向数据流，每个类只干一件事。`Main` 只负责解析参数和组装流水线。

## 易错点提示（不看第二遍需求也值得想清楚）

1. **重名冲突**：`a.jpg` 和已存在的 `images/a.jpg` 冲突怎么办？建议追加序号
   （`a-1.jpg`），比覆盖安全，比跳过友好。
2. **dry-run 的实现位置**：不要在 Main 里 `if (dryRun) print else move` 到处分支——
   把 `dryRun` 传给 `FileMover`，让它在移动前打印意图并跳过实际移动。
   想更优雅？定义 `MoveAction` 接口，dry-run 和普通模式各一个实现（策略模式雏形）。
3. **分类规则硬编码还是配置化**：先用 `Map<String, String>`（扩展名→类别）构造器注入
   就行，好测试。别一上来读配置文件，过度设计。
4. **大小写**：`.JPG` 和 `.jpg` 应该是同一类。
5. **只移动一层还是递归**：建议递归扫描，但**跳过已经分类好的目录**（比如目标目录
   本身就在扫描根下，别把 `images/` 里的文件再搬一次）。
6. **异常路径**：目录不存在、无权限、目标已存在且冲突——每个都要有明确行为，
   不能放任抛栈。

## 测试建议

- `@TempDir`（JUnit 5 自带）在临时目录里造文件结构，测 Scanner 和 Mover，
  不碰真实文件系统。
- `FileClassifier` 是纯逻辑，最适合参数化测试（`@CsvSource`：扩展名→类别）。
- 断言移动结果时用 `Files.exists` 验证。

## 打包运行

```bash
mvn package
java -jar target/file-organizer-1.0-SNAPSHOT.jar <目录> [--dry-run]
```
