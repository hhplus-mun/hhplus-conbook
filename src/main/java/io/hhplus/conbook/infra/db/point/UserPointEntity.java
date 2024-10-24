package io.hhplus.conbook.infra.db.point;

import io.hhplus.conbook.domain.point.UserPoint;
import io.hhplus.conbook.infra.db.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "user_point")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class UserPointEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private long point;
    private LocalDateTime updatedTime;

    public UserPoint toDomain() {
        return new UserPoint(id, user.toDomain(), point, updatedTime);
    }
}
