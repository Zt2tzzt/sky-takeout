# Spring Task

## 一、Spring Task 介绍

Spring Task 是 Spring 框架提供的任务调度工具，可以按照约定的时间，自动执行某个代码逻辑。

定时任务的使用场景有：

- 信用卡每月还款提醒；
- 银行贷款每月还款提醒；
- 火车票售票系统处理未支付订单；
- 入职纪念日为用户发送通知。

## 二、Cron 表达式

Cron 表达式，就是一个字符串，通过 Cron 表达式可以定义任务触发的时间。

- 构成规则：分为 6 或 7 个域，每个域由空格分隔开，含义分别为：秒、分钟、小时、日、月、周、年（可选）
- 举例：2024 年 11 月 14 日上午 9 点整，对应的 Cron 表达式为：**0 0 9 14 11 ? 2024**\

> 一般“日”和“周“的值，其中一个设置，另一个用 ？表示，不同时设置；

特殊情况：描述 2 月份的最后一天，可能是 28 号，也可能是 29 号，所以不能写具体数字。

- 为了描述这些信息，可使用一些特殊的字符。
- 这些特殊情况，不用自行手写，可在[在线生成器](https://cron.qqe2.com/)中生成。

## 三、入门案例

Spring Task 使用步骤：

Ⅰ、导入 spring-context 的 Maven 坐标（已在 spring-boot-starter 中传递）；

Ⅱ、启动类添加注解 `@EnableScheduling`；

sky-takeout-backend/sky-server/src/main/java/com/sky/SkyApplication.java

```java
package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement // 开启注解方式的事务管理
@Slf4j
@EnableCaching
@EnableScheduling
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
```

Ⅲ、自定义定时任务。

创建 task 包，在其中创建 MyTask 类。

sky-takeout-backend/sky-server/src/main/java/com/sky/task/MyTask.java

```java
package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class MyTask {
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask() {
        log.info("定时任务执行了~{}", new Date());
    }
}
```

- 在类上，加上 `@Component` 注解，表示该类要注入到 IOC 容器中。

Ⅳ、执行启动类，就会开启定时任务。
