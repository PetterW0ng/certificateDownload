package org.pkucare.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.pkucare.annotation.RequestLimit;
import org.pkucare.exception.RequestLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 访问限制组件
 * Created by weiqin on 2019/10/24.
 */
@Aspect
@Component
public class RequestLimitContract {
    private static final Logger logger = LoggerFactory.getLogger(RequestLimitContract.class);

    @Resource
    private CacheManager cacheManager;

    /**
     * 定义切面范围
     */
    @Pointcut("execution(public * org.pkucare.controller.*.*(..))")
    public void webPointCut() {
    }

    @Before("webPointCut() && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint, RequestLimit limit) throws RequestLimitException {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest) {
                request = (HttpServletRequest) args[i];
                break;
            }
        }
        if (request == null) {
            throw new RequestLimitException("方法中缺失HttpServletRequest参数");
        }
        String ip = request.getLocalAddr();
        String url = request.getRequestURL().toString();
        String phone = request.getParameter("phone");
        String key = "reqlimit" + "_" + url + "_" + ip + "_" +phone;

        Cache requestLimitCache = cacheManager.getCache("requestLimit");
        Cache.ValueWrapper valueWrap = requestLimitCache.get(key);
        if (valueWrap == null) {
            requestLimitCache.put(key, 1);
            logger.info("url={}, count={}",key, 1);
        } else {
            int count = Integer.parseInt(valueWrap.get().toString()) + 1;
            requestLimitCache.put(key, count);
            logger.info("url={}, count={}",key, count);
            if (count > limit.count()) {
                throw new RequestLimitException();
            }
        }

    }
}