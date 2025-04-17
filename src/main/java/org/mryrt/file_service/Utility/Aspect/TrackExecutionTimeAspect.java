package org.mryrt.file_service.Utility.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.mryrt.file_service.Utility.Message.Global.GlobalLogMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@ConditionalOnProperty(name = "monitoring.track-execution-time.enabled", havingValue = "true")
public class TrackExecutionTimeAspect {

    @Around("@annotation(ignoredTrackExecutionTime) || @within(ignoredTrackExecutionTime)")
    public Object measureAndLogExecutionTime(ProceedingJoinPoint joinPoint, TrackExecutionTime ignoredTrackExecutionTime) throws Throwable {

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long durationNanos = System.nanoTime() - startNanos;

        logExecutionMetrics(joinPoint.getSignature(), durationNanos);

        return result;
    }

    private void logExecutionMetrics(Signature methodSignature, long durationNanos) {
        GlobalLogMessage.EXECUTION_TIME.log(methodSignature.toShortString(), formatDuration(durationNanos));
    }

    private String formatDuration(long nanos) {
        Duration duration = Duration.ofNanos(nanos);

        if (duration.toMinutes() > 0) {
            return String.format("%d m %d s", duration.toMinutes(), duration.toSecondsPart());
        }
        if (duration.toSeconds() > 0) {
            return String.format("%.3f s", duration.toNanos() / 1_000_000_000.0);
        }
        if (duration.toMillis() > 0) {
            return String.format("%.3f ms", duration.toNanos() / 1_000_000.0);
        }
        if (duration.toNanos() > 1_000) {
            return String.format("%.3f μs", duration.toNanos() / 1_000.0);
        }
        return String.format("%d ns", duration.toNanos());
    }

}