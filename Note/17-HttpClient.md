# HttpClient

## 一、HttpClient 是什么

HttpClient 是 Apache Jakarta Common 下的子项目，可以用来提供高效的、最新的、功能丰富的支持 HTTP 协议的客户端编程工具包，

HttpClient 支持 HTTP 协议最新的版本和建议。

## 二、HttpClient 依赖引入

在 Maven 构建的 Spring Boot 项目中，引入 HttpClient 依赖的坐标。

sky-takeout-backend/sky-server/pom.xml

```xml
<!-- http -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.13</version>
</dependency>
```

> 阿里云的 aliyun-sdk-oss 依赖中，传递了 HttpClient 的依赖。
>
> ```xml
> <dependency>
>     <groupId>com.aliyun.oss</groupId>
>     <artifactId>aliyun-sdk-oss</artifactId>
> </dependency>
> ```

## 三、HttpClient 的核心 API

- `HttpClient`：Http 客户端对象类型，使用该类型对象可发起 Http 请求。
- `HttpClients`：可认为是构建器，可创建 `HttpClient` 对象。
- `CloseableHttpClient`：实现类，实现了 `HttpClient` 接口。
- `HttpGet`：Get 方式请求类型。
- `HttpPost`：Post 方式请求类型。

## 四、HttpClient 发送请求步骤

- 创建 `HttpClient` 对象；
- 创建 `HttpGet` / `HttpPost` / … 请求对象；
- 调用 `HttpClient` 的 `execute` 方法发送请求。

## 五、HttpClient 的请求

### 5.1.GET 请求发送

HttpClient 发送 GET 请求：

sky-takeout-backend/sky-server/src/test/java/com/sky/HttpClientTest.java

```java
package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(classes = SkyApplication.class)
@Slf4j
public class HttpClientTest {
    /**
     * 此方法用于，测试 GET 请求
     */
    @Test
    public void test1() throws IOException {
        // 创建 HttpClient 对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建请求对象
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");

        // 发送请求，接收响应结果
        CloseableHttpResponse response = httpClient.execute(httpGet);

        // 获取服务端返回的响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        log.info("响应状态码：" + statusCode);

        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        log.info("响应结果：" + body);

        response.close();
        httpClient.close();
    }
}
```

### 5.2.POST 请求发送

HttpClient 发送 POST 请求：

sky-takeout-backend/sky-server/src/test/java/com/sky/HttpClientTest.java

```java
……

/**
 * 此方法用于，测试 POST 请求
 */
@Test
public void testPost() throws IOException {
    // 创建 HttpClient 对象
    CloseableHttpClient httpClient = HttpClients.createDefault();

    // 创建请求对象
    HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("username", "admin");
    jsonObject.put("password", "123456");

    StringEntity entity = new StringEntity(jsonObject.toJSONString());
    // 指定请求编码方式
    entity.setContentEncoding("utf-8");
    // 指定请求体格式
    entity.setContentType("application/json");

    httpPost.setEntity(entity);

    // 发送请求
    CloseableHttpResponse response = httpClient.execute(httpPost);

    // 解析返回结果
    HttpEntity entity1 = response.getEntity();
    String body1 = EntityUtils.toString(entity1);
    log.info("响应结果：" + body1);

    // 关闭资源
    response.close();
    httpClient.close();
}

……
```

## 六、HttpClient 工具类

为 HttpClient 封装一个工具类。

sky-takeout-backend/sky-common/src/main/java/com/sky/utils/HttpClientUtil.java

```java
package com.sky.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http工具类
 */
public class HttpClientUtil {
    private static final int TIMEOUT_MSEC = 5 * 1000;

    /**
     * 发送GET方式请求
     *
     * @param url      url
     * @param paramMap 参数
     * @return String
     */
    public static String doGet(String url, Map<String, String> paramMap) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String result = "";
        CloseableHttpResponse response = null;

        try {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    builder.addParameter(key, paramMap.get(key));
                }
            }
            URI uri = builder.build();

            //创建GET请求
            HttpGet httpGet = new HttpGet(uri);

            //发送请求
            response = httpClient.execute(httpGet);

            //判断响应状态
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 发送POST方式请求
     *
     * @param url      url
     * @param paramMap 参数
     * @return String
     */
    public static String doPost(String url, Map<String, String> paramMap) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 创建参数列表
            if (paramMap != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            try {
                if (response != null) response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    /**
     * 发送POST方式请求
     *
     * @param url      url
     * @param paramMap 参数
     * @return String
     */
    public static String doPost4Json(String url, Map<String, String> paramMap) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            if (paramMap != null) {
                //构造json格式数据
                JSONObject jsonObject = new JSONObject();
                jsonObject.putAll(paramMap);
                StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            try {
                if (response != null) response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    private static RequestConfig builderRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MSEC)
                .setConnectionRequestTimeout(TIMEOUT_MSEC)
                .setSocketTimeout(TIMEOUT_MSEC).build();
    }
}
```
