package com.deepcraft.organizer;

import java.nio.file.Path;
import java.util.Map;

/** 根据扩展名决定文件属于哪个类别 */
public class FileClassifier {

    private final Map<String, String> rules;   // 扩展名（小写） -> 类别名

    public FileClassifier(Map<String, String> rules) {
        this.rules = rules;
    }

    /**
     * @return 类别名；无扩展名或规则表未覆盖时返回 "others"
     */
    public String categoryOf(Path file) {
        // TODO: 取扩展名 -> 转小写 -> 查规则表，查不到给 "others"。
        //       注意 ".gitignore" 这种以点开头但没有扩展名的文件。
        throw new UnsupportedOperationException("待实现");
    }
}
