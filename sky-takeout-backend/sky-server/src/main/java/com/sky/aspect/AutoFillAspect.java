package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 次自定义切面类，用于实现公共字段自动填充逻辑。
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    /**
     * 此方法用于：在前置通知中，为公共字段赋值。
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始公共字段自动填充...");

        // 获取当前被拦截方法上数据库的操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象（使用向下转型）
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获取方法上的注解对象
        OperationType operationType = autoFill.value(); // 获取数据库操作类型

        // 获取当前被拦截方法上的参数（实体对象）（约定实体对象是方法的第一个参数）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;
        Object entity = args[0];

        // 装备自动填充的数据（时间、修改人 Id）
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据不同的操作类型，为字段赋值
        switch (operationType) {
            case INSERT -> {
                try {
                    Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                    Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                    setCreateTime.invoke(entity, now);
                    setCreateUser.invoke(entity, currentId);
                    setUpdateTime.invoke(entity, now);
                    setUpdateUser.invoke(entity, currentId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case UPDATE -> {
                try {
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                    setUpdateTime.invoke(entity, now);
                    setUpdateUser.invoke(entity, currentId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }
}
