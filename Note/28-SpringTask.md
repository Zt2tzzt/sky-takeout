来单提醒和催单业务场景如下，要使用 WebSocket

- 来单提醒：用户下单并完成支付后，后端管理系统界面，要进行语音播报的来单提醒。‘
- 催单：用户下单并完成支付后，商家迟迟未接单，当用户点击“催单”后，在后端管理系统界面，进行语音播报的来单提醒。

# Spring Task

## 一、Spring Task 介绍

Spring Task 是 Spring 框架提供的任务调度工具，可以按照约定的时间，自动执行某个代码逻辑。

定时任务的使用场景有：

- 信用卡每月还款提醒；
- 银行贷款每月还款提醒；
- 火车票售票系统处理未支付订单；
- 入职纪念日为用户发送通知。

## 二、Cron 表达式

cron表达式，就是一个字符串，通过 cron 表达式可以定义任务触发的时间。

构成规则：分为 6 或 7 个域，由空格分隔开，每个域的含义分别为：秒、分钟、小时、日、月、周、年(可选)

举例：2024 年 11 月 14 日上午 9 点整，对应的 cron 表达式为：**0 0 9 14 11 ? 2024**\

> 般“日”和“周“的值，其中一个设置，另一个用 ？表示，不同时设置；

特殊情况：描述 2 月份的最后一天，可能是 28 号，也可能是 29 号，所以就不能写具体数字。为了描述这些信息，可使用一些特殊的字符。

- 这些特殊情况，不用自行手写，可在[在线生成器](https://cron.qqe2.com/)中生成。

## 三、入门案例

Spring Task 使用步骤：

1.导入 spring-context（已在 spring-boot-starter 中传递）的 Maven 坐标；

2.启动类添加注解 `@EnableScheduling`；

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
@EnableTransactionManagement //开启注解方式的事务管理
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

3.自定义定时任务。

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

4.执行启动类，开启定时任务。
