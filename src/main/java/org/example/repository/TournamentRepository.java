package org.example.repository;

import org.example.annotation.Cumponent;
import org.example.model.Tournament;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Cumponent
@Repository
public class TournamentRepository {

    private final ConcurrentMap<Long, Tournament> tournaments =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

    public Tournament save(Tournament tournament) {
        if (tournament.getId() == null) {
            tournament.setId(nextId.getAndIncrement());
        }

        tournaments.put(tournament.getId(), tournament);
        return tournament;
    }

    public List<Tournament> findAll() {
        return List.copyOf(tournaments.values());
    }

    public Optional<Tournament> findById(Long id) {
        return Optional.ofNullable(tournaments.get(id));
    }

    public boolean deleteById(Long id) {
        return tournaments.remove(id) != null;
    }
}