package com.sky;

import com.sky.controller.user.DishController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@Slf4j
@SpringBootTest
class SkyApplicationTests {
    @Autowired
    private DishController dishController;
    @Test
    public void contextLoads() {
        String res = DigestUtils.md5DigestAsHex("123456".getBytes());
        log.info("加密后：{}", res);
    }

    @Test
    public void testListWithFlavorByCategoryId() {
        dishController.list(20L);
    }
}
