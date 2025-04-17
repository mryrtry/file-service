package org.mryrt.file_service.Utility.Aspect;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mryrt.file_service.Utility.Annotation.RateLimited;
import org.mryrt.file_service.Utility.Exceptions.RateLimitedException;
import org.mryrt.file_service.Utility.Message.Global.GlobalErrorMessage;
import org.mryrt.file_service.Utility.Service.RateLimiterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "rate-limiting.enable", havingValue = "true")
public class RateLimitingAspect {

    private final RateLimiterService rateLimiterService;

    @Around("@annotation(rateLimited)")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = getClientKey(joinPoint);
        Bucket bucket = rateLimiterService.resolveBucket(key, rateLimited.maxRequests(), rateLimited.timeWindowSeconds());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            throw new RateLimitedException(GlobalErrorMessage.RATE_LIMITED_ENDPOINT, waitForRefill);
        }

        return joinPoint.proceed();
    }

    private String getClientKey(ProceedingJoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        return "%s:%s:%s".formatted(request.getRemoteAddr(), className, methodName);
    }

}