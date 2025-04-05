package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.sky.constant.AutoFillConstant.*;
import static com.sky.enumeration.OperationType.UPDATE;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoPoint() {}

    @Before("autoPoint()")
    public void autoFill(JoinPoint joinPoint) throws Throwable {
        //记录日志显示当前方法
        log.info("AutoFill start");
        log.info("当前代理方法: {}", joinPoint.getSignature().getName());
        //获取方法数据库操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        //获取实体对象
        Object entity = joinPoint.getArgs()[0];
        Class<?> entityClass = entity.getClass();
        //从自定义注解判断数据库操作为更新
        if (autoFill.value().equals(UPDATE)) {
            //获取实体对象相应方法
            Method setUpdateTime = entityClass.getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entityClass.getMethod(SET_UPDATE_USER, Long.class);
            //解决方法权限
            setUpdateTime.setAccessible(true);
            setUpdateUser.setAccessible(true);
            //调用方法为实体对象赋值
            setUpdateTime.invoke(entity, LocalDateTime.now());
            setUpdateUser.invoke(entity, BaseContext.getCurrentId());
        } else {
            //获取实体对象相应方法
            Method setUpdateTime = entityClass.getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entityClass.getMethod(SET_UPDATE_USER, Long.class);
            Method setCreateTime = entityClass.getMethod(SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = entityClass.getMethod(SET_CREATE_USER, Long.class);
            //解决方法权限
            setUpdateTime.setAccessible(true);
            setUpdateUser.setAccessible(true);
            setCreateTime.setAccessible(true);
            setCreateUser.setAccessible(true);
            //调用方法为实体对象赋值
            setUpdateTime.invoke(entity, LocalDateTime.now());
            setUpdateUser.invoke(entity, BaseContext.getCurrentId());
            setCreateTime.invoke(entity, LocalDateTime.now());
            setCreateUser.invoke(entity, BaseContext.getCurrentId());

        }

    }

}
