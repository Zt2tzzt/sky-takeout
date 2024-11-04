package com.sky;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
}
