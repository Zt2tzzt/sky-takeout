package com.sky;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
public class MyTest {
    @Test
    public void testMd5() {
        String res = DigestUtils.md5DigestAsHex("123456".getBytes());
        System.out.println(res);
    }
}
