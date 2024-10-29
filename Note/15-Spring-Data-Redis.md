# Spring Data Redis

## 一、Redis 的 Java 客户端

在 java 程序中，操作 Redis需要使用 Redis 的 Java 客户端，就像使用 JDBC 操作 MySQL 数据库一样。

Redis 的 Java 客户端很多，常用的几种：

- Jedis
- Lettuce
- Spring Data Redis

Spring 对 Redis 客户端 Jedis、Lettuce 进行了整合，提供了 Spring Data Redis，

- Spring Boot 项目中还提供了对应的 Starter，即 spring-boot-starter-data-redis。

## 二、Spring Data Redis 引入

[Spring Data Redis](https://spring.io/projects/spring-data-redis) 是 Spring 的一部分，提供了在 Spring 应用中通过简单的配置，就可以访问 Redis 服务，对 Redis 底层开发包进行了高度封装。

在 Spring 项目中，可以使用 Spring Data Redis 来简化 Redis 操作。

步骤一：在基于 Maven 构建的 Spring Boot 项目中，引入 Spring Data Redis 的起步依赖。

sky-takeout-backend/sky-server/pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

步骤二：在项目配置文件中，进行 Redis 相关配置：

sky-takeout-backend/sky-server/src/main/resources/application.yml

```yaml
spring:
  ……
  data:
    redis:
      host: localhost
      port: 6379
      password: wee1219
      database: 0
```

> Redis 启动时，默认创建了 16 个数据库，分别是 db0 - db15；

步骤三：编写配置类，创建 `RedisTemplate` 对象

Spring Data Redis 中提供了一个高度封装的类：`RedisTemplate`，对相关 api 进行了归类封装，将同一类型操作封装为 operation 接口，具体分类如下：

- `ValueOperations`：string 数据操作。
- `SetOperations`：set 类型数据操作。
- `ZSetOperations`：zset 类型数据操作。
- `HashOperations`：hash 类型的数据操作。
- `ListOperations`：list 类型的数据操作。

sky-takeout-backend/sky-server/src/main/java/com/sky/config/RedisConfiguration.java

```java
package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("开始创建 RedisTemplate 对象...");

        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 设置 Redis 连接工厂对象
        redisTemplate.setConnectionFactory(connectionFactory);
        // 设置 Redis key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
```

- 设置 Redis 连接工厂对象，Spring data redis 起步依赖会创建连接工厂的 Bean 对象，并放入 IOC 容器中。
- 设置 key 的序列化器后，用 Java 程序存储 key 时不会出现乱码。

> 当前配置类不是必须的，因为 Spring Boot 框架会自动装配 `RedisTemplate` 对象，但是默认的key序列化器为 `JdkSerializationRedisSerializer`，导致存到 Redis 后的数据和原始数据有差别，故设置为 `StringRedisSerializer` 序列化器。

## 三、Spring Data Redis 使用

### 1.String 类型操作

sky-takeout-backend/sky-server/src/test/java/com/sky/SpringDataRedisTest.java

```java
package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void testString() {
        // SET
        redisTemplate.opsForValue().set("city", "北京");

        // GET
        String city = (String) redisTemplate.opsForValue().get("city");
        log.info("city: {}", city);

        // SETEX
        redisTemplate.opsForValue().set("code", "1234", 3, TimeUnit.MINUTES);

        // SETNX
        redisTemplate.opsForValue().setIfAbsent("name", "wee");
        redisTemplate.opsForValue().setIfAbsent("name", "tamako");
    }
}
```

- 因为设置 key 的序列化器，可以为键传入对象（Object），会被序列化为字符串存入 Redis。
