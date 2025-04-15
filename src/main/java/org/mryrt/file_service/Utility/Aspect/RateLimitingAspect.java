package org.mryrt.file_service.Utility.Aspect;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mryrt.file_service.Utility.Annotation.RateLimited;
import org.mryrt.file_service.Utility.Service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@Slf4j
public class RateLimitingAspect {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Around("@annotation(rateLimited)")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = getClientKey();
        Bucket bucket = rateLimiterService.resolveBucket(
                key,
                rateLimited.maxRequests(),
                rateLimited.timeWindowSeconds()
        );
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many requests. Please try again in " + waitForRefill + " seconds."
            );
        }

        return joinPoint.proceed();
    }

    private String getClientKey() {
        var requestAttributes = org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes();
        var request = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();
        return request.getRemoteAddr();
    }

}