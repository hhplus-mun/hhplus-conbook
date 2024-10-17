package io.hhplus.conbook.domain.point;


import io.hhplus.conbook.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserPoint {
    private Long id;
    private User user;
    private long point;
    private LocalDateTime updatedTime;

    public void updatePointWith(long total, LocalDateTime reqTime) {
        point = total;
        updatedTime = reqTime;
    }
}
