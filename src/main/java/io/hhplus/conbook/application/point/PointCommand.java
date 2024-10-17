package io.hhplus.conbook.application.point;

import java.time.LocalDateTime;

public class PointCommand {
    public record Charge(
            long userId,
            long amount,
            LocalDateTime reqTime
    ) {}

    public record Balance(
            long userId
    ) {}
}
