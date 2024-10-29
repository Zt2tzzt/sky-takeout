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
    public void test1() {
        log.info("开始测试 Spring Data Redis...");
        log.info("redisTemplate: {}", redisTemplate);

        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue(); // //string 数据操作
        log.info("valueOperations: {}", valueOperations);
        HashOperations<Object, Object, Object> hahsOperation = redisTemplate.opsForHash(); // hash 数据操作
        log.info("hahsOperation: {}", hahsOperation);
        ListOperations<Object, Object> listOperation = redisTemplate.opsForList(); // list 数据操作
        log.info("listOperation: {}", listOperation);
        SetOperations<Object, Object> setOpearion = redisTemplate.opsForSet(); // set 数据操作
        log.info("setOpearion: {}", setOpearion);
        ZSetOperations<Object, Object> zsetOperation = redisTemplate.opsForZSet(); // zset 数据操作
        log.info("zsetOperation: {}", zsetOperation);
    }

    @Test
    public void testString() {
        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue(); // //string 数据操作

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
