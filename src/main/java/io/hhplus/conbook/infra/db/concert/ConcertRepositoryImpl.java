package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public Concert getConcertBy(long id) {
        return concertJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException())
                .toDomain();
    }

    @Override
    public List<Concert> getConcertList() {
        return concertJpaRepository.findAll()
                .stream()
                .map(ConcertEntity::toDomain)
                .toList();
    }
}
