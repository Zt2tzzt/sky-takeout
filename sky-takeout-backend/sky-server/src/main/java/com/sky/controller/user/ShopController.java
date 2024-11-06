package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
@Tag(name = "店铺相关接口")
public class ShopController {
    private static final String KEY = "SHOP_STATUS";

    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public ShopController(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/status")
    @Operation(summary = "查询营业状态")
    public Result<Integer> queryStatus() {
        log.info("查询营业状态");
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);

        return Result.success(shopStatus);
    }
}