package cn.edu.sdu.wh.lqy.lingxi.blog.config;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

/**
 * Log Aop
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    @Pointcut("execution(public * cn.edu.sdu.wh.lqy.lingxi.blog.controller..*.*(..))")
    public void logCut() {
    }

    @Before("logCut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        LOGGER.info("Request-url: " + request.getRequestURL().toString() + ", IP : " + request.getRemoteAddr() + ",Class_Method : "
                + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()
                + ",Args : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "object", pointcut = "logCut()")
    public void doAfterReturning(Object object) throws Throwable {
        // 处理完请求，返回内容
        LOGGER.info("Response : " + object);
    }

//    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
//        User user = (User) SecurityUtils.getSubject().getPrincipal();
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        SysLog log = new SysLog();
//        Log logAnnotation = method.getAnnotation(Log.class);
//        if (logAnnotation != null) {
//            log.setOperation(logAnnotation.value());
//        }
//        String className = joinPoint.getTarget().getClass().getName();
//        String methodName = signature.getName();
//        log.setMethod(className + "." + methodName + "()");
//        Object[] args = joinPoint.getArgs();
//        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
//        String[] paramNames = u.getParameterNames(method);
//        if (args != null && paramNames != null) {
//            String params = "";
//            for (int i = 0; i < args.length; i++) {
//                params += "  " + paramNames[i] + ": " + args[i];
//            }
//            log.setParams(params);
//        }
//        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
//        log.setIp(IPUtils.getIpAddr(request));
//        log.setUsername(user.getUsername());
//        log.setTime(time);
//        log.setCreateTime(new Date());
//        log.setLocation(AddressUtils.getRealAddressByIP(log.getIp(), mapper));
//        this.logService.save(log);
//    }
}
