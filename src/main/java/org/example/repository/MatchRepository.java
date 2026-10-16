package org.example.repository;

import org.example.annotation.Cumponent;
import org.example.model.Match;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Cumponent
@Repository
public class MatchRepository {

    private final ConcurrentMap<Long, Match> matches =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

    public Match save(Match match) {
        if (match.getId() == null) {
            match.setId(nextId.getAndIncrement());
        }

        matches.put(match.getId(), match);
        return match;
    }

    public List<Match> findAll() {
        return List.copyOf(matches.values());
    }

    public Optional<Match> findById(Long id) {
        return Optional.ofNullable(matches.get(id));
    }

    public boolean deleteById(Long id) {
        return matches.remove(id) != null;
    }
}