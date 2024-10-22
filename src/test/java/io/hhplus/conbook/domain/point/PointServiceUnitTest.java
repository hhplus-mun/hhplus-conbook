package io.hhplus.conbook.domain.point;

import io.hhplus.conbook.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceUnitTest {

    @InjectMocks
    PointService pointService;

    @Mock
    UserPointRepository userPointRepository;

    @Test
    @DisplayName("[실패]: 중복요청시 실패처리")
    void duplication() {
        //given
        long userId = 1L;
        User james = new User(userId, "james", UUID.randomUUID().toString());
        LocalDateTime dateTime = LocalDateTime.now();
        UserPoint userPoint = new UserPoint(1L, james, 1000, dateTime);

        given(userPointRepository.findPointWithPessimisticLock(userId))
                .willReturn(userPoint);

        //when & then
        assertThatThrownBy(() -> pointService.chargePoint(userId, 100, dateTime))
                .isInstanceOf(NotValidRequestException.class);
    }
}