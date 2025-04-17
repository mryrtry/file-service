package org.mryrt.file_service.Utility.Service;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, int maxRequests, int timeWindowSeconds) {
        return buckets.computeIfAbsent(key, ignored -> createNewBucket(maxRequests, timeWindowSeconds));
    }

    private Bucket createNewBucket(int maxRequests, int timeWindowSeconds) {
        Refill refill = Refill.intervally(maxRequests, java.time.Duration.ofSeconds(timeWindowSeconds));
        Bandwidth limit = Bandwidth.classic(maxRequests, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

}
