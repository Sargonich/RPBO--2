package org.example.repository;

import org.example.annotation.Cumponent;
import org.example.model.TeamApplication;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Cumponent
@Repository
public class TeamApplicationRepository {

    private final ConcurrentMap<Long, TeamApplication> applications =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

    public TeamApplication save(TeamApplication application) {
        if (application.getId() == null) {
            application.setId(nextId.getAndIncrement());
        }

        applications.put(application.getId(), application);
        return application;
    }

    public List<TeamApplication> findAll() {
        return List.copyOf(applications.values());
    }

    public Optional<TeamApplication> findById(Long id) {
        return Optional.ofNullable(applications.get(id));
    }

    public boolean deleteById(Long id) {
        return applications.remove(id) != null;
    }
}