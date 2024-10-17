package io.hhplus.conbook.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("[정상]: 사용자 조회 정상동작 확인 테스트")
    void getUser() {
        // given
        long userId = 1L;

        // when
        User user = userService.getUser(userId);

        // then
        assertThat(user.getId()).isEqualTo(userId);
    }
}