package io.hhplus.conbook.domain.concert;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConcertServiceUnitTest {

    @InjectMocks
    ConcertService concertService;

    @Mock
    ConcertRepository concertRepository;
    @Mock
    ConcertScheduleRepository scheduleRepository;
    @Mock
    SeatRepository seatRepository;

    @Test
    @DisplayName("[정상]: 이용가능한 좌석만 filter처리해서 뿌려주는지 확인")
    void availableSeats() {
        // given
        List<ConcertSchedule> schedules = Arrays.asList(
                new ConcertSchedule(1L, null, LocalDate.now(), 1, 5),
                new ConcertSchedule(1L, null, LocalDate.now(), 2, 5),
                new ConcertSchedule(1L, null, LocalDate.now(), 3, 5),
                new ConcertSchedule(1L, null, LocalDate.now(), 4, 5),
                new ConcertSchedule(1L, null, LocalDate.now(), 5, 5),
                new ConcertSchedule(1L, null, LocalDate.now(), 6, 5),
                new ConcertSchedule(1L, null, LocalDate.now(), 7, 5)
        );
        BDDMockito.given(scheduleRepository.findScheduleListBy(1L))
                .willReturn(schedules);

        // when
        List<ConcertSchedule> availableList = concertService.getAvailableConcertScheduleList(1L);

        // then
        assertThat(availableList.size()).isLessThan(schedules.size());
    }
}