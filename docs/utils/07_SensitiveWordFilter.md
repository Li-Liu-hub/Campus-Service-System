# 敏感词过滤器 - SensitiveWordFilter

## 类简介

`SensitiveWordFilter` 是基于**DFA（Deterministic Finite Automaton，确定有限状态自动机）**算法实现的敏感词过滤工具。通过构建敏感词字典树，实现高效的文章内容敏感词检测、替换和提取。

## 核心功能

1. **敏感词检测** - 判断敏感词
2. **敏感词提取文本中是否包含** - 提取文本中所有敏感词
3. **敏感词替换** - 将敏感词替换为星号 `*`

## 算法原理

### DFA 算法简介

DFA（确定有限状态自动机）是一种用于字符串匹配的高效算法。其核心思想是：
- 将所有敏感词构建为一个**字典树（Trie Tree）**
- 扫描文本时，沿着字典树向下匹配
- 如果匹配到末尾（end=true），说明发现敏感词

### 字典树结构

```
                    root
                   / | \
                  p  s  a
                 /   |   \
                u    h    d
               /     |     \
              l      i     m
             /       |       \
            l        t        i
           /                 \
          *                   n
         (end=true)
```

敏感词词库：`["pull", "shit", "admin"]`

## 核心方法详解

### 1. 初始化加载敏感词
```java
/**
 * 初始化敏感词库
 * 项目启动时自动执行，从文件加载敏感词
 */
@PostConstruct
public void init() {
    try {
        // 1. 加载敏感词文件
        Resource resource = resourceLoader.getResource("classpath:sensitive-words.txt");
        if (!resource.exists()) {
            return;
        }

        // 2. 逐行读取敏感词
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    addWord(word);  // 添加到字典树
                }
            }
        }
    } catch (Exception ignored) {
    }
}
```

**敏感词文件格式**（`resources/sensitive-words.txt`）：
```
fuck
shit
admin
密码
银行卡
作弊
```

### 2. 添加敏感词到字典树
```java
/**
 * 将敏感词添加到字典树
 * @param word 敏感词
 */
private void addWord(String word) {
    Node node = root;
    for (char c : word.toCharArray()) {
        // 如果子节点不存在，创建新节点
        node = node.children.computeIfAbsent(c, k -> new Node());
    }
    // 标记为结束节点
    node.end = true;
}

/**
 * 字典树节点
 */
private static class Node {
    private final Map<Character, Node> children = new HashMap<>();
    private boolean end;  // 是否为敏感词结尾
}
```

### 3. 过滤敏感词
```java
/**
 * 过滤敏感词
 * 将敏感词替换为星号，返回过滤后的文本和是否包含敏感词的标记
 *
 * @param text 待过滤的文本
 * @return 过滤结果对象
 */
public SensitiveFilterResult filter(String text) {
    if (text == null || text.isEmpty()) {
        return new SensitiveFilterResult(text, false);
    }

    char[] chars = text.toCharArray();
    boolean matched = false;
    int length = chars.length;

    // 遍历文本的每个字符
    for (int i = 0; i < length; i++) {
        Node node = root;
        int j = i;
        int lastMatch = -1;

        // 从当前位置开始匹配
        while (j < length) {
            node = node.children.get(chars[j]);
            if (node == null) {
                break;  // 匹配失败
            }

            // 匹配成功，记录位置
            if (node.end) {
                lastMatch = j;
            }
            j++;
        }

        // 如果找到敏感词，替换为星号
        if (lastMatch >= i) {
            matched = true;
            for (int k = i; k <= lastMatch; k++) {
                chars[k] = '*';
            }
            // 跳过后，继续匹配
            i = lastMatch;
        }
    }

    return new SensitiveFilterResult(new String(chars), matched);
}
```

### 4. 检测是否包含敏感词
```java
/**
 * 检测文本中是否包含敏感词
 *
 * @param text 待检测的文本
 * @return true-包含敏感词，false-不包含
 */
public boolean containsSensitiveWord(String text) {
    if (text == null || text.isEmpty()) {
        return false;
    }
    SensitiveFilterResult result = filter(text);
    return result.hasSensitive();
}
```

### 5. 提取敏感词
```java
/**
 * 提取文本中的所有敏感词
 *
 * @param text 待检测的文本
 * @return 敏感词列表（去重）
 */
public List<String> getSensitiveWords(String text) {
    if (text == null || text.isEmpty()) {
        return Collections.emptyList();
    }

    List<String> foundWords = new ArrayList<>();
    char[] chars = text.toCharArray();
    int length = chars.length;

    // 遍历文本
    for (int i = 0; i < length; i++) {
        Node node = root;
        int j = i;
        StringBuilder matchedWord = new StringBuilder();

        // 尝试匹配
        while (j < length) {
            node = node.children.get(chars[j]);
            if (node == null) {
                break;
            }

            matchedWord.append(chars[j]);

            // 如果是敏感词结尾，记录
            if (node.end) {
                foundWords.add(matchedWord.toString());
            }
            j++;
        }
    }

    // 去重并返回
    return foundWords.stream().distinct().collect(Collectors.toList());
}
```

### 6. 替换敏感词
```java
/**
 * 将文本中的敏感词替换为星号
 *
 * @param text 待处理的文本
 * @return 替换后的文本
 */
public String maskSensitiveWord(String text) {
    if (text == null || text.isEmpty()) {
        return text;
    }
    SensitiveFilterResult result = filter(text);
    return result.getFilteredText();
}
```

## 过滤流程图

```
┌─────────────────────────────────────────────────────────┐
│                 敏感词过滤流程                            │
└─────────────────────────────────────────────────────────┘

   输入文本："fuck you admin"
       │
       ▼
┌─────────────────┐
│  初始化变量     │
│  chars = [...]  │
│  matched = false│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  i = 0, 遍历字符│
│  'f' -> 匹配    │
└────────┬────────┘
         │
    ┌────┴────┐
    │ 继续匹配 │
    ▼         ▼
┌────────┐ ┌────────┐
│ 'u'    │ │ 'c'    │
│ 匹配   │ │ 匹配   │
└────┬───┘ └───┬────┘
     │         │
     ▼         ▼
┌────────┐ ┌────────┐
│ 'k'    │ │ end=true│
│ end=true│ ←──发现敏感词!
└────┬───┘ └───┬────┘
     │         │
     ▼         ▼
┌─────────────────────┐
│  替换 f u c k → ****│
│  matched = true    │
└────────┬────────────┘
         │
         ▼
┌─────────────────┐
│  继续遍历       │
│  "**** you..." │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  返回结果       │
│  filteredText  │
│  hasSensitive  │
└─────────────────┘
```

## 使用示例

### 1. 注入 Service 使用
```java
/**
 * 帖子服务
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    /**
     * 发布帖子
     */
    @Override
    public void publish(PostPublishDTO dto, Integer userId) {
        // 1. 检测标题是否包含敏感词
        if (dto.getTitle() != null &&
            sensitiveWordFilter.containsSensitiveWord(dto.getTitle())) {
            List<String> words = sensitiveWordFilter.getSensitiveWords(dto.getTitle());
            throw new IllegalArgumentException("标题包含敏感词：" + String.join(", ", words));
        }

        // 2. 检测内容是否包含敏感词
        if (dto.getContent() != null &&
            sensitiveWordFilter.containsSensitiveWord(dto.getContent())) {
            List<String> words = sensitiveWordFilter.getSensitiveWords(dto.getContent());
            throw new IllegalArgumentException("内容包含敏感词：" + String.join(", ", words));
        }

        // 3. 过滤并保存（可选）
        // String filteredContent = sensitiveWordFilter.maskSensitiveWord(dto.getContent());

        // 继续业务逻辑...
    }
}
```

### 2. 动态添加敏感词
```java
/**
 * 敏感词管理服务
 */
@Service
public class SensitiveWordService {

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    /**
     * 添加敏感词
     */
    public void addWord(String word) {
        // 1. 保存到数据库
        SensitiveWord sw = new SensitiveWord();
        sw.setWord(word);
        sensitiveWordMapper.insert(sw);

        // 2. 动态添加到过滤器
        sensitiveWordFilter.initSensitiveWordMap(Set.of(word));
    }
}
```

## 性能优化

### 时间复杂度
- **敏感词检测**: O(n)，n 为文本长度
- **敏感词提取**: O(n × m)，m 为最大敏感词长度

### 空间复杂度
- **字典树**: O(k)，k 为所有敏感词字符总数

### 优化建议
1. **启动预加载** - 敏感词库在应用启动时一次性加载到内存
2. **DFA算法** - O(n) 时间复杂度，性能高效
3. **缓存结果** - 对于相同内容可缓存检测结果

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-server/src/main/java/com/jsyl/utils/SensitiveWordFilter.java` | 敏感词过滤器 |
| `jsyl-server/src/main/java/com/jsyl/utils/SensitiveFilterResult.java` | 过滤结果类 |
| `jsyl-server/src/main/resources/sensitive-words.txt` | 敏感词词库 |
| `jsyl-server/src/main/java/com/jsyl/service/impl/PostServiceImpl.java` | 帖子服务（使用过滤） |
| `jsyl-server/src/main/java/com/jsyl/service/impl/OrderCenterServiceImpl.java` | 订单服务（使用过滤） |
