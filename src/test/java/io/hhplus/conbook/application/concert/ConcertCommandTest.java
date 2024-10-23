package io.hhplus.conbook.application.concert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConcertCommandTest {

    @Test
    @DisplayName("[정상]: ConcertCommand.Status 초기화")
    void status() {
        // given
        long concertId = 1;
        String date = "20241024";

        // when
        ConcertCommand.Status status = new ConcertCommand.Status(concertId, date);

        // then
        assertThat(status.date()).isEqualTo("20241024");
    }

    @Test
    @DisplayName("[예외]: ConcertCommand.Status 초기화 실패")
    void statusException() {
        // given
        long concertId = 1;
        String date = "20241024-exception";

        // when & then
        assertThatThrownBy(() -> new ConcertCommand.Status(concertId, date))
                .isInstanceOf(IllegalArgumentException.class);
    }
}