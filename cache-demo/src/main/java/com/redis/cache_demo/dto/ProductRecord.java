package com.redis.cache_demo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductRecord(
        UUID id,
        String name,
        double price,
        int items,
        LocalDateTime createdDt,
        LocalDateTime updatedDt,
        int version
) implements Serializable {
}

