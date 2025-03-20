# Spring Cache 框架、缓存套餐

## 一、Spring Cache

Spring Cache 是一个框架，实现了基于注解的缓存功能，只需要简单地加一个注解，就能实现缓存功能。

Spring Cache 提供了一层抽象，底层可以切换不同的缓存实现，例如：

- EHCache
- Caffeine
- Redis（常用）

在项目中，引入 Spring Cache 依赖的坐标：

sky-takeout-backend/sky-server/pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 1.1.Spring Cache 常用注解

在 Spring Cache 中提供了很多缓存操作的注解，常见的有以下几个：

| **注解**         | **说明**                                                     |
| ---------------- | ------------------------------------------------------------ |
| `@EnableCaching` | 开启缓存注解功能，通常加在启动类上                           |
| `@Cacheable`     | 通常加方法上，在方法执行前，先查询缓存中是否有数据，有则直接返回缓存数据；没有则调用方法并将方法返回值放到缓存中。 |
| `@CachePut`      | 通常加方法上，将方法的返回值放到缓存中                       |
| `@CacheEvict`    | 通常加方法上，将一条或多条数据从缓存中删除                   |

在 Spring Boot 项目中，使用缓存技术，只需在项目中导入相关缓存技术的依赖包，并在启动类上使用 `@EnableCaching` 开启缓存支持即可。

- 比如：使用 Redis 作为缓存技术，只需要导入Spring data Redis 的 maven 坐标即可。

## 二、Spring Cache 的使用

### 2.1.@EnableCaching 注解

在启动类上，加入 `@EnableCaching` 注解。

sky-takeout-backend/sky-server/src/main/java/com/sky/SkyApplication.java

```java
……
@EnableCaching
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
```

### 2.2.@CachePut 注解

Controller 层示例代码：@CachePut 注解的使用；

```java
@PostMapping
@CachePut(cacheNames = "UserCache", key = "#user.id") // keu：UserCache::1
public User save(@RequestBody user) {
    userMapper.insert(user);
    return user;
}
```

- `cacheNames` 属性可以随意填写，一般填写与业务相关的名称。
- `key` 属性中，使用 SpEL（Spring Expression Language）语法，表示 Spring 表达式；指定键名；格式有：
  - `#参数名.属性值`；
  - `#result.属性名`，表示返回值的属性名。
  - `#p0.属性名` / `#a0.属性名` / `#root.args[0].属性名`，表示第一个参数的属性名；

> Redis 中存储的 key，可以形成树形结构；表现形式就是：`层级1:层级2:key名称`

### 2.3.@Cacheable 注解

Controller 层示例代码：@Cacheable 注解的使用；

```java
@GetMapping
@Cachedble(cacheNames = "userCache", key = "#id")
public User getById(Long id) {
    User user = userMapper.getById(id);
    return user;
}
```

- `cacheNames` 属性指定缓存中已有的名称。
- `key` 属性，使用 SpEL，指定要读取和存储的缓存键名；格式只能是：`#参数名`

> Spring Cache 底层封装了所在类的代理对象（比如 Controller 类）
>
> - 当要执行 Controller 中的方法时，会先执行代理对象中的方法，去缓存中查找数据；

### 2.4.@CacheEvict 注解

Controller 层示例代码：@CacheEvict 注解的使用；

```java
// 删除一条数据
@DeleteMapping
@CacheEvict(cacheNames = "userCache", key = "#id")
public void deleteById(Long id) {
    userMapper.deleteById(id);
}

// 删除所有数据
@DeleteMapping("/delAll")
@CacheEvict(cacheNames = "userCache", allEntries = true)
public void deleteAll() {
    userMapper.deleteAll();
}
```

- `cacheNames` 属性指定缓存中已有的名称。
- `key` 属性，使用 SpEL，指定要删除的缓存键名；格式只能是：`#参数名`。
- `allEntries` 属性，表示所有的属性；

> 原理也是 Spring Cache 底层封装的代理对象。

## 三、缓存套餐代码开发

具体实现步骤如下：

### 3.1.导入坐标

导入 Spring Cache 和 Redis 相关 Maven 依赖坐标（已导入）；

sky-takeout-backend/sky-server/pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 3.2.启动类上加 @EnableCaching 注解

在启动类上加入 `@EnableCaching` 注解，开启缓存注解功能；

sky-takeout-backend/sky-server/src/main/java/com/sky/SkyApplication.java

```java
package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement // 开启注解方式的事务管理
@Slf4j
@EnableCaching
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
```

### 3.3.用户端接口调整

在用户端接口 `SetmealController` 的 `list` 方法上，加上 `@Cacheable` 注解；

sky-takeout-backend/sky-server/src/main/java/com/sky/controller/user/SetmealController.java

```java
……

/**
 * 此方法用于：条件查询
 *
 * @param categoryId 分类 id
 * @return <List<Setmeal>>
 */
@GetMapping("/list")
@Operation(summary = "根据分类id查询套餐")
@Cacheable(cacheNames = "setmealCache", key = "#categoryId")
public Result<List<Setmeal>> list(Long categoryId) {
    Setmeal setmeal = new Setmeal();
    setmeal.setCategoryId(categoryId);
    setmeal.setStatus(StatusConstant.ENABLE);

    List<Setmeal> list = setmealService.list(setmeal);
    return Result.success(list);
}

……
```

- 返回值 `Result.success(list)` 会被自动存入 Redis 缓存中。

### 3.4.管理端接口调整

在管理端接口 `SetmealController` 的 `save`、`delete`、`update`、`startOrStop` 等方法上，加上 `@CacheEvict` 注解。

sky-takeout-backend/sky-server/src/main/java/com/sky/controller/admin/SetmealController.java

```java
……

@PostMapping
@Operation(summary = "新增套餐")
@CachePut(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
    int num = setmealService.saveWithDish(setmealDTO);
    return Result.success("成功插入" + num + "条数据");
}

@DeleteMapping
@Operation(summary = "删除套餐")
@CacheEvict(cacheNames = "setmealCache", allEntries = true)
public Result<String> deleteById(List<Long> ids) {
    int i = setmealService.deleteBatch(ids);
    return i > 0 ? Result.success("成功删除" + i + "条数据") : Result.error("删除失败");
}

@PutMapping
@Operation(summary = "修改套餐")
@CacheEvict(cacheNames = "setmealCache", allEntries = true)
public Result<String> modify(@RequestBody SetmealDTO setmealDTO) {
    int i = setmealService.modify(setmealDTO);
    return i > 0 ? Result.success("成功修改" + i + "条数据") : Result.error("修改失败");
}

@PostMapping("/status/{status}")
@CacheEvict(cacheNames = "setmealCache", allEntries = true)
public Result<String> startOrStop(@PathVariable int status, Long id) {
    int i = setmealService.startOrStop(status, id);
    return i > 0 ? Result.success("成功修改" + i + "条数据") : Result.error("修改失败");
}

……
```
