package io.hhplus.conbook.infra.db.user;

import io.hhplus.conbook.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String name;

    @Column(unique = true)
    private String uuid;

    public User toDomain() {
        return new User(id, name, uuid);
    }

    public UserEntity(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.uuid = user.getUuid();
    }
}
