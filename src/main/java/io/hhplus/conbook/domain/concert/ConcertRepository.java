package io.hhplus.conbook.domain.concert;

import java.util.List;

public interface ConcertRepository {
    Concert getConcertBy(long id);
    List<Concert> getConcertList();
}
