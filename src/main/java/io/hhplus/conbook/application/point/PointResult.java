package io.hhplus.conbook.application.point;

import java.time.LocalDateTime;

public class PointResult {
    public record Charge(
            long userId,
            long point,
            LocalDateTime updatedTime
    ) {}

    public record Balance(
            long userId,
            long point,
            LocalDateTime lastUpdatedTime
    ) {}
}
