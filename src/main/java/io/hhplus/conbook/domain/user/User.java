package io.hhplus.conbook.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private Long id;
    private String name;
    private String uuid;

    public User(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }
}
