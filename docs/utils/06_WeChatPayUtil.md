# 微信支付工具类 - WeChatPayUtil

## 类简介

`WeChatPayUtil` 是微信支付工具类，基于微信支付 V3 版 API 实现。提供统一下单、支付签名、退款申请等核心功能。

## 核心功能

1. **JSAPI 支付** - 小程序公众号支付
2. **支付签名** - 生成唤起微信支付所需的签名
3. **申请退款** - 发起退款请求

## 常量定义

```java
// 微信支付下单接口地址（JSAPI）
public static final String JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

// 申请退款接口地址
public static final String REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";
```

## 核心方法详解

### 1. 获取 HTTP 客户端
```java
/**
 * 获取配置好签名验证的 HTTP 客户端
 * 自动处理签名和验签
 */
private CloseableHttpClient getClient() {
    PrivateKey merchantPrivateKey = null;
    try {
        // 1. 加载商户API私钥
        merchantPrivateKey = PemUtil.loadPrivateKey(
            new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath()))
        );

        // 2. 加载微信支付平台证书
        X509Certificate x509Certificate = PemUtil.loadCertificate(
            new FileInputStream(new File(weChatProperties.getWeChatPayCertFilePath()))
        );
        List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);

        // 3. 创建 HTTP 客户端构建器
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(
                    weChatProperties.getMchid(),           // 商户号
                    weChatProperties.getMchSerialNo(),     // 商户证书序列号
                    merchantPrivateKey                      // 商户私钥
                )
                .withWechatPay(wechatPayCertificates);     // 微信平台证书

        // 4. 构建 HttpClient（自动处理签名和验签）
        CloseableHttpClient httpClient = builder.build();
        return httpClient;

    } catch (FileNotFoundException e) {
        e.printStackTrace();
        return null;
    }
}
```

### 2. POST 请求方法
```java
/**
 * 发送 POST 请求
 */
private String post(String url, String body) throws Exception {
    CloseableHttpClient httpClient = getClient();

    // 创建 POST 请求
    HttpPost httpPost = new HttpPost(url);

    // 设置请求头
    httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
    httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());

    // 设置请求体
    httpPost.setEntity(new StringEntity(body, "UTF-8"));

    // 发送请求
    CloseableHttpResponse response = httpClient.execute(httpPost);

    try {
        // 获取响应内容
        String bodyAsString = EntityUtils.toString(response.getEntity());
        return bodyAsString;
    } finally {
        httpClient.close();
        response.close();
    }
}
```

### 3. 统一下单（JSAPI）
```java
/**
 * JSAPI 统一下单
 * 生成预支付交易单
 *
 * @param orderNum 商户订单号
 * @param total 支付金额（元）
 * @param description 商品描述
 * @param openid 用户openid
 * @return 预支付交易会话标识
 */
private String jsapi(String orderNum, BigDecimal total, String description, String openid) {
    // 构建请求参数
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("appid", weChatProperties.getAppid());           // 应用ID
    jsonObject.put("mchid", weChatProperties.getMchid());           // 商户号
    jsonObject.put("description", description);                     // 商品描述
    jsonObject.put("out_trade_no", orderNum);                       // 商户订单号
    jsonObject.put("notify_url", weChatProperties.getNotifyUrl());   // 通知地址

    // 金额单位转换：元 -> 分
    JSONObject amount = new JSONObject();
    amount.put("total", total.multiply(new BigDecimal(100))
        .setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
    amount.put("currency", "CNY");  // 货币类型

    jsonObject.put("amount", amount);

    // 支付者信息
    JSONObject payer = new JSONObject();
    payer.put("openid", openid);
    jsonObject.put("payer", payer);

    // 发送请求
    String body = jsonObject.toJSONString();
    return post(JSAPI, body);
}
```

### 4. 发起支付
```java
/**
 * 发起微信支付
 * 返回小程序调起支付所需的参数
 *
 * @param orderNum 商户订单号
 * @param total 支付金额
 * @param description 商品描述
 * @param openid 用户openid
 * @return 支付参数（包含时间戳、随机串、预支付ID、签名等）
 */
public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
    // 1. 统一下单，获取预支付ID
    String bodyAsString = jsapi(orderNum, total, description, openid);

    // 2. 解析返回结果
    JSONObject jsonObject = JSON.parseObject(bodyAsString);
    System.out.println(jsonObject);

    String prepayId = jsonObject.getString("prepay_id");

    // 3. 如果获取到预支付ID，进行二次签名
    if (prepayId != null) {
        // 生成时间戳和随机串
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = RandomStringUtils.randomNumeric(32);

        // 4. 构造签名原文
        ArrayList<Object> list = new ArrayList<>();
        list.add(weChatProperties.getAppid());          // appId
        list.add(timeStamp);                             // 时间戳
        list.add(nonceStr);                              // 随机字符串
        list.add("prepay_id=" + prepayId);              // 预支付交易会话标识

        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : list) {
            stringBuilder.append(o).append("\n");
        }
        String signMessage = stringBuilder.toString();
        byte[] message = signMessage.getBytes();

        // 5. 使用私钥进行RSA签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(PemUtil.loadPrivateKey(
            new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath()))
        ));
        signature.update(message);
        String packageSign = Base64.getEncoder().encodeToString(signature.sign());

        // 6. 返回支付参数给小程序
        JSONObject jo = new JSONObject();
        jo.put("timeStamp", timeStamp);
        jo.put("nonceStr", nonceStr);
        jo.put("package", "prepay_id=" + prepayId);
        jo.put("signType", "RSA");
        jo.put("paySign", packageSign);

        return jo;
    }

    // 返回错误信息
    return jsonObject;
}
```

### 5. 申请退款
```java
/**
 * 申请退款
 *
 * @param outTradeNo 商户订单号
 * @param outRefundNo 商户退款单号
 * @param refund 退款金额
 * @param total 订单总金额
 * @return 退款结果
 */
public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception {
    JSONObject jsonObject = new JSONObject();

    // 商户订单号和退款单号
    jsonObject.put("out_trade_no", outTradeNo);
    jsonObject.put("out_refund_no", outRefundNo);

    // 金额信息（单位：分）
    JSONObject amount = new JSONObject();
    amount.put("refund", refund.multiply(new BigDecimal(100))
        .setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
    amount.put("total", total.multiply(new BigDecimal(100))
        .setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
    amount.put("currency", "CNY");

    jsonObject.put("amount", amount);
    jsonObject.put("notify_url", weChatProperties.getRefundNotifyUrl());

    // 发送退款请求
    String body = jsonObject.toJSONString();
    return post(REFUNDS, body);
}
```

## 支付流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                    微信支付流程                                  │
└─────────────────────────────────────────────────────────────────┘

  用户在小程序下单
       │
       ▼
┌─────────────────┐
│  商户服务器      │
│  调用 pay()     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  统一下单 API   │
│  jsapi()       │
│  /v3/pay/      │
│  transactions/ │
│  jsapi         │
└────────┬────────┘
         │
         ▼
    ┌────────────┐
    │ 微信返回   │
    │ prepay_id │
    └─────┬──────┘
          │
          ▼
┌─────────────────┐
│  二次签名       │
│  SHA256withRSA │
└────────┬────────┘
          │
          ▼
┌─────────────────┐
│  返回支付参数   │
│  timeStamp     │
│  nonceStr      │
│  package       │
│  paySign       │
└────────┬────────┘
          │
          ▼
┌─────────────────┐
│  小程序调起    │
│  wx.requestPay │
│  ment()       │
└────────┬────────┘
          │
          ▼
    ┌────────────┐
    │ 支付成功   │
    │ 回调通知   │
    └────────────┘
```

## 配置说明

### 1. 配置属性类
```java
/**
 * 微信支付配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.pay")
public class WeChatProperties {
    private String appid;                  // 应用ID
    private String mchid;                  // 商户号
    private String mchSerialNo;           // 商户证书序列号
    private String privateKeyFilePath;    // 商户私钥文件路径
    private String weChatPayCertFilePath; // 微信平台证书文件路径
    private String notifyUrl;              // 支付回调通知地址
    private String refundNotifyUrl;        // 退款回调通知地址
}
```

### 2. application.yml 配置
```yaml
wechat:
  pay:
    appid: your-appid
    mchid: your-mchid
    mch-serial-no: your-serial-no
    private-key-file-path: /path/to/apiclient_key.pem
    wechat-pay-cert-file-path: /path/to/wechatpay_cert.pem
    notify-url: https://your-domain.com/pay/notify
    refund-notify-url: https://your-domain.com/pay/refund-notify
```

## 使用示例

```java
/**
 * 订单支付服务
 */
@Service
public class PaymentService {

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 创建支付订单
     */
    public JSONObject createPayment(Order order, String openid) throws Exception {
        // 调用微信支付
        return weChatPayUtil.pay(
            order.getOrderNo(),              // 商户订单号
            order.getOrderAmount(),          // 支付金额
            "校园服务平台-" + order.getTypeName(), // 商品描述
            openid                            // 用户openid
        );
    }

    /**
     * 申请退款
     */
    public String refundOrder(Order order, BigDecimal refundAmount) throws Exception {
        return weChatPayUtil.refund(
            order.getOrderNo(),               // 商户订单号
            "REF" + order.getOrderNo(),        // 退款单号
            refundAmount,                      // 退款金额
            order.getOrderAmount()             // 订单金额
        );
    }
}
```

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-common/src/main/java/com/jsyl/utils/WeChatPayUtil.java` | 微信支付工具类 |
| `jsyl-common/src/main/java/com/jsyl/properties/WeChatProperties.java` | 微信配置属性 |
| `jsyl-server/src/main/java/com/jsyl/service/impl/OrderCenterServiceImpl.java` | 订单服务（支付相关） |
