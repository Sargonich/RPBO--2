package org.example.repository;

import org.example.annotation.Cumponent;
import org.example.model.Team;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Cumponent
@Repository
public class TeamRepository {

    private final ConcurrentMap<Long, Team> teams =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

    public Team save(Team team) {
        if (team.getId() == null) {
            team.setId(nextId.getAndIncrement());
        }

        teams.put(team.getId(), team);
        return team;
    }

    public List<Team> findAll() {
        return List.copyOf(teams.values());
    }

    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(teams.get(id));
    }

    public boolean deleteById(Long id) {
        return teams.remove(id) != null;
    }
}