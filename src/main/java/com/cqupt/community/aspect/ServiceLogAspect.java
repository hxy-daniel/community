package com.cqupt.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.ClientEndpoint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AOP统一记录日志
 */
@Component
//@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * 切点，处理哪些对象的哪些方法
     */
    @Pointcut("execution(* com.cqupt.community.service.*.*(..))")
    public void serviceLogPointCut() {

    }

    /**
     * 方法执行前处理日志逻辑
     * @param joinPoint
     */
    @Before("serviceLogPointCut()")
    public void writeLog(JoinPoint joinPoint) {
        // 日志格式：用户ip在time访问了method方法
        // 获取ip
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 处理非controller请求，如kafka
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String ip = request.getRemoteHost();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s]在[%s]访问了[%s]方法.", ip, time, method));
    }
}
