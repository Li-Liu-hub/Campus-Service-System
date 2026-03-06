# 阿里云 OSS 工具类 - AliOssUtil

## 类简介

`AliOssUtil` 是文件上传工具类，基于阿里云对象存储服务（OSS）实现。用于将图片、文件等资源上传到阿里云OSS，并返回可访问的URL地址。

## 核心功能

1. **文件上传** - 将字节数组上传到阿里云OSS
2. **URL生成** - 自动生成可访问的文件URL

## 类结构

```java
@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {
    private String endpoint;        // OSS访问域名
    private String accessKeyId;    // AccessKey ID
    private String accessKeySecret; // AccessKey Secret
    private String bucketName;      // 存储桶名称
}
```

## 核心方法详解

### 文件上传方法
```java
/**
 * 上传文件到阿里云OSS
 * @param bytes 文件字节数组
 * @param objectName OSS存储对象名称（路径+文件名）
 * @return 文件的访问URL
 */
public String upload(byte[] bytes, String objectName) {
    // 1. 创建OSS客户端
    OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    try {
        // 2. 上传文件到OSS
        // 参数：存储桶名称、对象名称、文件内容
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));

    } catch (OSSException oe) {
        // OSS异常处理
        throw new RuntimeException("文件上传失败: " + oe.getErrorMessage(), oe);
    } catch (ClientException ce) {
        // 客户端异常处理
        throw new RuntimeException("文件上传失败: " + ce.getMessage(), ce);
    } finally {
        // 3. 关闭OSS客户端，释放资源
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    // 4. 构建并返回文件访问URL
    StringBuilder stringBuilder = new StringBuilder("https://");
    stringBuilder
            .append(bucketName)      // 存储桶名称
            .append(".")
            .append(endpoint)         // 端点域名
            .append("/")
            .append(objectName);      // 文件路径

    return stringBuilder.toString();
}
```

## 上传流程图

```
┌─────────────────────────────────────────────────────────┐
│                    文件上传流程                           │
└─────────────────────────────────────────────────────────┘

  本地文件
     │
     ▼
┌─────────────────┐
│  读取为字节数组  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 创建OSSClient   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  ossClient.putObject()                                │
│  - bucketName: 存储桶名称                             │
│  - objectName: 对象名称                               │
│  - content: 字节流                                    │
└────────┬────────┘
         │
    ┌────┴────┐
    │ 成功    │  失败
    ▼         ▼
┌─────────┐ ┌─────────────┐
│ 关闭OSS │ │ 抛出异常    │
└────┬────┘ └─────────────┘
     │
     ▼
┌────────────────────────────────────────┐
│  生成访问URL                            │
│  https://bucketName.endpoint/objectName │
└────────────────┬───────────────────────┘
                 │
                 ▼
           返回URL字符串
```

## 配置说明

### 1. 配置类
```java
/**
 * 阿里云OSS配置类
 */
@Configuration
public class OssConfiguration {

    @Autowired
    private AliOssProperties aliOssProperties;

    /**
     * 创建OSS工具类Bean
     */
    @Bean
    public AliOssUtil aliOssUtil() {
        return new AliOssUtil(
            aliOssProperties.getEndpoint(),
            aliOssProperties.getAccessKeyId(),
            aliOssProperties.getAccessKeySecret(),
            aliOssProperties.getBucketName()
        );
    }
}
```

### 2. 配置属性类
```java
/**
 * 阿里云OSS配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliOssProperties {
    private String endpoint;        // 例如：oss-cn-hangzhou.aliyuncs.com
    private String accessKeyId;    // 你的AccessKey ID
    private String accessKeySecret; // 你的AccessKey Secret
    private String bucketName;      // 存储桶名称
}
```

### 3. application.yml 配置
```yaml
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name
```

## 使用示例

### 1. 控制器中使用
```java
/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<String> uploadImage(MultipartFile file) {
        try {
            // 1. 获取文件字节数组
            byte[] bytes = file.getBytes();

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = "images/" + UUID.randomUUID().toString() + extension;

            // 3. 上传到OSS
            String url = aliOssUtil.upload(bytes, objectName);

            // 4. 返回文件URL
            return Result.success(url);

        } catch (Exception e) {
            return Result.error("文件上传失败");
        }
    }
}
```

### 2. 文件名生成策略
```java
/**
 * 生成OSS对象名称
 */
public class FileNameUtil {

    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String originalFilename) {
        // 获取文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 按日期分类存储
        LocalDate today = LocalDate.now();
        String datePath = String.format("%d/%02d/%02d/",
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth());

        // 生成唯一文件名：日期路径 + UUID + 扩展名
        return datePath + UUID.randomUUID().toString() + extension;
    }
}
```

## OSS 存储结构示例

```
your-bucket-name/
├── images/
│   ├── 2026/
│   │   └── 03/
│   │       └── 06/
│   │           ├── a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
│   │           └── b2c3d4e5-f6a7-8901-bcde-f23456789012.png
│   └── avatars/
│       └── user_12345.jpg
├── posts/
│   └── ...
└── orders/
    └── ...
```

## 注意事项

1. **endpoint 格式** - 需要使用内网或外网 endpoint，根据服务器位置选择
2. **Bucket 权限** - 建议设置为"公共读"，或使用签名URL
3. **文件大小** - 单个文件大小限制根据OSS套餐而定
4. **费用** - OSS按存储空间和流量收费

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-common/src/main/java/com/jsyl/utils/AliOssUtil.java` | OSS工具类 |
| `jsyl-common/src/main/java/com/jsyl/properties/AliOssProperties.java` | OSS配置属性 |
| `jsyl-server/src/main/java/com/jsyl/config/OssConfiguration.java` | OSS配置类 |
| `jsyl-server/src/main/java/com/jsyl/controller/UploadController.java` | 文件上传控制器 |
