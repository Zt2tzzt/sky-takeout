package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Tag(name = "店铺相关接口")
public class ShopController {
    private static final String KEY = "SHOP_STATUS";

    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public ShopController(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PutMapping("/{status}")
    @Operation(summary = "修改营业状态")
    public Result<Null> modifyStatus(@PathVariable Integer status) {
        log.info("修改营业状态为：{}", status);

        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @GetMapping("/status")
    @Operation(summary = "查询营业状态")
    public Result<Integer> queryStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("查询营业状态为：{}", shopStatus);

        return Result.success(shopStatus);
    }
}
