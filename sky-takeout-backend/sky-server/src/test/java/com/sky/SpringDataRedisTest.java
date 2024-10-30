package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
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

        Boolean idDel = redisTemplate.delete("mylist");
        log.info("idDel: {}", idDel); // idDel: true
    }
}
