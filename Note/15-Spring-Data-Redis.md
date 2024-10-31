# Spring Data Redis

## 一、Redis 的 Java 客户端

在 java 程序中，操作 Redis 需要使用 Redis 的 Java 客户端，就像使用 JDBC 操作 MySQL 数据库一样。

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

> 当前配置类不是必须的，因为 Spring Boot 框架会自动装配 `RedisTemplate` 对象，但是默认的 key 序列化器为 `JdkSerializationRedisSerializer`，导致存到 Redis 里的数据和原始数据有差别，故设置为 `StringRedisSerializer` 序列化器。

## 三、Spring Data Redis 使用

### 1.字符串（String）类型操作

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
        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue(); // string 数据操作

        // SET
        valueOperations.set("city", "北京");

        // GET
        String city = (String) valueOperations.get("city");
        log.info("city: {}", city); // city: 北京

        // SETEX
        valueOperations.set("code", "1234", 3, TimeUnit.MINUTES);

        // SETNX
        Boolean isSet1 = valueOperations.setIfAbsent("name", "wee");
        Boolean isSet2 = valueOperations.setIfAbsent("name", "tamako");
        log.info("isSet1: {}, isSet2: {}", isSet1, isSet2); // isSet1: true, isSet2: false
    }
}
```

- 因为设置了 key 的序列化器，所以可以为键传入对象（Object），它会被序列化为字符串存入 Redis。

### 2.哈希（Hash）类型操作

sky-takeout-backend/sky-server/src/test/java/com/sky/SpringDataRedisTest.java

```java
package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void testHash() {
        HashOperations<Object, Object, Object> hashOperations = redisTemplate.opsForHash();

        // HSET
        hashOperations.put("100", "name", "wee");
        hashOperations.put("100", "age", "26");

        // HGET
        String name = (String) hashOperations.get("100", "name");
        log.info("name: {}", name); // name: wee

        // HKEYS
        Set<Object> keys = hashOperations.keys("100");
        log.info("keys: {}", keys); // keys: [name, age]

        // HVALS
        List<Object> values = hashOperations.values("100");
        log.info("values: {}", values); // values: [wee, 26]

        // HDEL
        Long idDelete = hashOperations.delete("100", "age");
        log.info("idDelete: {}", idDelete); // idDelete: 1
    }
}
```

### 3.列表（List）类型操作

sky-takeout-backend/sky-server/src/test/java/com/sky/SpringDataRedisTest.java

```java
package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void testList() {
        ListOperations<Object, Object> listOperations = redisTemplate.opsForList();

        // LPUSH
        Long size1 = listOperations.leftPushAll("mylist", new String[]{"a"}, new String[]{"b"}, new String[]{"c"});
        Long size2 = listOperations.leftPush("mylist", "d");
        log.info("size1: {}, size2: {}", size1, size2); // size1: 3, size2: 4

        // LRANGE
        List<Object> mylist = listOperations.range("mylist", 0, -1);
        log.info("mylist: {}", mylist); // mylist: [d, [Ljava.lang.String;@5edd2170, [Ljava.lang.String;@327ac23, [Ljava.lang.String;@32db94fb]

        // RPOP
        Object popEle = listOperations.rightPop("mylist");
        log.info("popEle: {}", popEle); // popEle: [a]

        // LLEN
        Long size = listOperations.size("mylist");
        log.info("size: {}", size); // size: 3
    }
}
```

### 3.集合（Set）类型操作

sky-takeout-backend/sky-server/src/test/java/com/sky/SpringDataRedisTest.java

```java
……

/**
 * 操作集合类型的数据
 */
@Test
public void testSet() {
    SetOperations<Object, Object> setOperations = redisTemplate.opsForSet();

    // SADD
    setOperations.add("set1", "a", "b", "c", "d");
    setOperations.add("set2", "a", "b", "x", "y");

    // SMEMBERS
    Set<Object> members = setOperations.members("set1");
    log.info("members: {}", members); // members: [a, b, c, d]

    // SCARD
    Long size = setOperations.size("set1");
    log.info("size: {}", size); // size: 4

    // SINTER 交集
    Set<Object> intersect = setOperations.intersect("set1", "set2");
    log.info("intersect: {}", intersect); // intersect: [a, b]

    // SUNION 并集
    Set<Object> union = setOperations.union("set1", "set2");
    log.info("union: {}", union); // union: [a, b, c, d, x, y]

    // SREM
    Long removeNum = setOperations.remove("set1", "a", "b");
    log.info("removeNum: {}", removeNum); // removeNum: 2
}

……
```

### 4.有序集合（ZSet）类型操作

sky-takeout-backend/sky-server/src/test/java/com/sky/SpringDataRedisTest.java

```java
……

/**
 * 操作有序集合类型的数据
 */
@Test
public void testZset() {
    ZSetOperations<Object, Object> zSetOperations = redisTemplate.opsForZSet();

    // ZADD
    Boolean isAdd1 = zSetOperations.add("zset1", "a", 10);
    Boolean idAdd2 = zSetOperations.add("zset1", "b", 12);
    Boolean isAdd3 = zSetOperations.add("zset1", "c", 9);
    log.info("isAdd1: {}, idAdd2: {}, isAdd3: {}", isAdd1, idAdd2, isAdd3); // isAdd1: true, idAdd2: true, isAdd3: true

    // ZRANGE
    Set<Object> zset1 = zSetOperations.range("zset1", 0, -1);
    log.info("zset1: {}", zset1); // zset1: [c, a, b]

    // ZINCRBY
    Double csore = zSetOperations.incrementScore("zset1", "c", 10);
    log.info("csore: {}", csore); // csore: 19.0

    // ZREM
    Long removNum = zSetOperations.remove("zset1", "a", "b");
    log.info("removNum: {}", removNum); // removNum: 2
}

……
```

### 5.通用操作

sky-takeout-backend/sky-server/src/test/java/com/sky/SpringDataRedisTest.java

```java
/**
 * 通用命令操作
 */
@Test
public void testCommon() {
    //KEYS EXISTS TYPE DEL
    Set<Object> keys = redisTemplate.keys("*");
    log.info("keys: {}", keys); // keys: [mylist, code, zset1, city, name]

    // EXISTS TYPE DEL
    Boolean name = redisTemplate.hasKey("name");
    log.info("name: {}", name); // name: true

    // EXISTS TYPE DEL
    Boolean set1 = redisTemplate.hasKey("set1");
    log.info("set1: {}", set1); // set1: false

    // TYPE
    assert keys != null;
    for (Object key : keys) {
        DataType type = redisTemplate.type(key);
        assert type != null;
        log.info("type.name(): {}", type.name());
    }
    // type.name(): LIST
    // type.name(): STRING
    // type.name(): ZSET
    // type.name(): STRING
    // type.name(): STRING

    Boolean isdDel = redisTemplate.delete("mylist");
    log.info("isdDel: {}", isdDel); // isdDel: true
}
```
