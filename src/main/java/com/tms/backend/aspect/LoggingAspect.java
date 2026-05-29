package com.tms.backend.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Autowired
    private ObjectMapper objectMapper;

    // Pointcut matches all methods in all classes under com.tms.backend.controller
    @Pointcut("execution(* com.tms.backend.controller..*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        HttpServletRequest request = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            request = attributes.getRequest();
        }

        String username = "Anonymous";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            username = authentication.getName();
        }

        String method = request != null ? request.getMethod() : "UNKNOWN";
        String url = request != null ? request.getRequestURI() : "UNKNOWN";
        String clientIp = request != null ? getClientIp(request) : "UNKNOWN";
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Safe extraction of arguments (avoiding circular references or massive objects if possible)
        String argsJson = "[]";
        try {
            // Only try to serialize if there are arguments
            if (joinPoint.getArgs().length > 0) {
                // Filter out non-serializable arguments like HttpServletRequest/Response if needed
                Object[] safeArgs = Arrays.stream(joinPoint.getArgs())
                        .filter(arg -> !(arg instanceof jakarta.servlet.ServletRequest) && !(arg instanceof jakarta.servlet.ServletResponse))
                        .toArray();
                argsJson = objectMapper.writeValueAsString(safeArgs);
                // Truncate if too long to prevent log flooding
                if (argsJson.length() > 2000) {
                    argsJson = argsJson.substring(0, 2000) + "... [TRUNCATED]";
                }
            }
        } catch (Exception e) {
            argsJson = "[UNSERIALIZABLE]";
        }

        // Log IN
        logger.info(">>> API_IN | User: {} | IP: {} | {} {} | Method: {}.{}() | Args: {}", 
                username, clientIp, method, url, className, methodName, argsJson);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - start;
            // Log ERROR
            logger.error("<<< API_ERROR | User: {} | {} {} | Time: {}ms | Exception: {}", 
                    username, method, url, executionTime, e.getMessage());
            throw e;
        }

        long executionTime = System.currentTimeMillis() - start;
        
        String resultJson = "";
        try {
            if (result != null) {
                resultJson = objectMapper.writeValueAsString(result);
                if (resultJson.length() > 2000) {
                    resultJson = resultJson.substring(0, 2000) + "... [TRUNCATED]";
                }
            }
        } catch (Exception e) {
            resultJson = "[UNSERIALIZABLE]";
        }

        // Log OUT
        logger.info("<<< API_OUT | User: {} | {} {} | Time: {}ms | Response: {}", 
                username, method, url, executionTime, resultJson);

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Extract first IP in case of multiple proxies
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
