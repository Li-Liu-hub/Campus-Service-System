# HTTP 客户端工具类 - HttpClientUtil

## 类简介

`HttpClientUtil` 是基于 Apache HttpClient 的 HTTP 请求工具类，封装了常用的 GET 和 POST 请求方法，用于与其他系统或第三方 API 进行交互。

## 核心功能

1. **GET 请求** - 支持带参数的 GET 请求
2. **POST 请求** - 支持表单形式的 POST 请求
3. **JSON POST 请求** - 支持 JSON 格式的 POST 请求
4. **超时控制** - 统一的请求超时配置（5秒）

## 常量定义

```java
// 请求超时时间：5秒
static final int TIMEOUT_MSEC = 5 * 1000;
```

## 核心方法详解

### 1. GET 请求
```java
/**
 * 发送 GET 请求
 * @param url 请求地址
 * @param paramMap 查询参数Map
 * @return 响应内容字符串
 */
public static String doGet(String url, Map<String,String> paramMap) {
    // 1. 创建默认的 HttpClient
    CloseableHttpClient httpClient = HttpClients.createDefault();

    String result = "";
    CloseableHttpResponse response = null;

    try {
        // 2. 构建带参数的 URI
        URIBuilder builder = new URIBuilder(url);
        if (paramMap != null) {
            for (String key : paramMap.keySet()) {
                builder.addParameter(key, paramMap.get(key));
            }
        }
        URI uri = builder.build();

        // 3. 创建 GET 请求
        HttpGet httpGet = new HttpGet(uri);

        // 4. 发送请求并获取响应
        response = httpClient.execute(httpGet);

        // 5. 判断响应状态码，200表示成功
        if (response.getStatusLine().getStatusCode() == 200) {
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 6. 关闭资源
        try {
            if (response != null) response.close();
            if (httpClient != null) httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    return result;
}
```

### 2. POST 请求（表单形式）
```java
/**
 * 发送 POST 请求（表单提交）
 * @param url 请求地址
 * @param paramMap 表单参数Map
 * @return 响应内容字符串
 */
public static String doPost(String url, Map<String, String> paramMap) throws IOException {
    // 1. 创建 HttpClient
    CloseableHttpClient httpClient = HttpClients.createDefault();
    CloseableHttpResponse response = null;
    String resultString = "";

    try {
        // 2. 创建 POST 请求
        HttpPost httpPost = new HttpPost(url);

        // 3. 构建表单参数
        if (paramMap != null) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (Map.Entry<String, String> param : paramMap.entrySet()) {
                paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            // 模拟表单提交
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
            httpPost.setEntity(entity);
        }

        // 4. 设置请求配置
        httpPost.setConfig(builderRequestConfig());

        // 5. 执行请求
        response = httpClient.execute(httpPost);

        // 6. 获取响应内容
        resultString = EntityUtils.toString(response.getEntity(), "UTF-8");

    } catch (Exception e) {
        throw e;
    } finally {
        try {
            if (response != null) response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    return resultString;
}
```

### 3. POST 请求（JSON 形式）
```java
/**
 * 发送 POST 请求（JSON格式）
 * @param url 请求地址
 * @param paramMap JSON参数Map
 * @return 响应内容字符串
 */
public static String doPost4Json(String url, Map<String, String> paramMap) throws IOException {
    // 1. 创建 HttpClient
    CloseableHttpClient httpClient = HttpClients.createDefault();
    CloseableHttpResponse response = null;
    String resultString = "";

    try {
        // 2. 创建 POST 请求
        HttpPost httpPost = new HttpPost(url);

        // 3. 构建 JSON 请求体
        if (paramMap != null) {
            // 使用 FastJSON 构建 JSON 对象
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> param : paramMap.entrySet()) {
                jsonObject.put(param.getKey(), param.getValue());
            }

            // 创建 StringEntity，设置编码和内容类型
            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            entity.setContentEncoding("utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        }

        // 4. 设置请求配置
        httpPost.setConfig(builderRequestConfig());

        // 5. 执行请求
        response = httpClient.execute(httpPost);

        // 6. 获取响应内容
        resultString = EntityUtils.toString(response.getEntity(), "UTF-8");

    } catch (Exception e) {
        throw e;
    } finally {
        try {
            if (response != null) response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    return resultString;
}
```

### 4. 请求配置构建
```java
/**
 * 构建请求配置
 * 设置连接超时、读取超时等
 */
private static RequestConfig builderRequestConfig() {
    return RequestConfig.custom()
            .setConnectTimeout(TIMEOUT_MSEC)         // 连接超时：建立连接等待时间
            .setConnectionRequestTimeout(TIMEOUT_MSEC) // 请求超时：从连接池获取连接时间
            .setSocketTimeout(TIMEOUT_MSEC)        // 读取超时：等待响应时间
            .build();
}
```

## 请求流程图

```
┌────────────────────────────────────────────────────────────┐
│                    HTTP 请求处理流程                        │
└────────────────────────────────────────────────────────────┘

  请求参数
     │
     ▼
┌─────────────────┐
│ 创建HttpClient  │ ◄── 使用 HttpClients.createDefault()
└────────┬────────┘
         │
    ┌────┴────┐
    │ GET     │   │ POST (表单)  │  │ POST (JSON)
    ▼         ▼   ▼              ▼  ▼
┌─────────┐ ┌─────────────┐ ┌─────────────┐
│URIBuilder│ │FormEntity   │ │StringEntity  │
│添加参数  │ │模拟表单提交 │ │构造JSON请求体│
└────┬────┘ └──────┬──────┘ └──────┬──────┘
     │             │              │
     └─────────────┴──────────────┘
                  │
                  ▼
         ┌────────────────┐
         │设置请求配置    │
         │超时: 5秒       │
         └────────┬───────┘
                  │
                  ▼
         ┌────────────────┐
         │ httpClient    │
         │ .execute()    │
         └────────┬───────┘
                  │
                  ▼
    ┌─────────────────────────┐
    │  判断状态码 (200成功)   │
    └────────────┬────────────┘
                 │
                 ▼
        ┌────────────────┐
        │EntityUtils    │
        │.toString()    │
        └────────┬───────┘
                 │
                 ▼
        ┌────────────────┐
        │  关闭资源      │
        │ response/     │
        │ httpClient   │
        └──────────────┘
```

## 使用示例

### 1. GET 请求示例
```java
// 调用第三方天气API
public String getWeather(String city) {
    String url = "http://api.weather.com/v1/forecast";

    Map<String, String> params = new HashMap<>();
    params.put("city", city);
    params.put("key", "your-api-key");

    String result = HttpClientUtil.doGet(url, params);
    return result;
}
```

### 2. POST 表单请求示例
```java
// 模拟表单登录
public String login(String username, String password) {
    String url = "http://example.com/login";

    Map<String, String> params = new HashMap<>();
    params.put("username", username);
    params.put("password", password);

    String result = HttpClientUtil.doPost(url, params);
    return result;
}
```

### 3. POST JSON 请求示例
```java
// 调用 REST API
public String createOrder(Order order) {
    String url = "http://api.example.com/orders";

    Map<String, String> params = new HashMap<>();
    params.put("orderNo", order.getOrderNo());
    params.put("amount", order.getAmount().toString());

    String result = HttpClientUtil.doPost4Json(url, params);
    return result;
}
```

## 配置超时时间

如果需要修改超时时间，可以修改常量值：

```java
// 在 HttpClientUtil 类中修改
static final int TIMEOUT_MSEC = 10 * 1000; // 10秒
```

或者改为动态参数：

```java
public static String doGet(String url, Map<String,String> paramMap, int timeout) {
    // 使用传入的 timeout 参数
}
```

## 注意事项

1. **资源释放** - 必须在 finally 块中关闭 response 和 httpClient
2. **编码统一** - 使用 UTF-8 编码避免中文乱码
3. **异常处理** - 建议根据业务需求处理异常
4. **连接池** - 高并发场景可使用连接池优化

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-common/src/main/java/com/jsyl/utils/HttpClientUtil.java` | HTTP客户端工具类 |
| `jsyl-server/src/main/java/com/jsyl/utils/WeChatPayUtil.java` | 微信支付（使用HttpClient） |
