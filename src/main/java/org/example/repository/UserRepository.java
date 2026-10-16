package org.example.repository;

import org.example.annotation.Cumponent;
import org.example.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Cumponent
@Repository
public class UserRepository {

    private final ConcurrentMap<Long, User> users =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId.getAndIncrement());
        }

        users.put(user.getId(), user);
        return user;
    }

    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }
}