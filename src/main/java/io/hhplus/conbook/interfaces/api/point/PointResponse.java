package io.hhplus.conbook.interfaces.api.point;

import java.time.LocalDateTime;

public class PointResponse {
    public record Charge(
            long userId,
            long point,
            LocalDateTime updateTime
    ) {}

    public record Point (
            long userId,
            long point,
            LocalDateTime lastUpdatedTime
    ) {}
}