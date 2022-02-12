package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.AbstractNamedEntity;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Integer, User> data = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    @Override
    public boolean delete(int id) {
        writeLock.lock();
        try {
            log.info("delete {}", id);
            return data.remove(id) == null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public User save(User user) {
        writeLock.lock();
        try {
            log.info("save {}", user);
            if (user.isNew()) {
                user.setId(counter.incrementAndGet());
                data.put(user.getId(), user);
                return user;
            }
            return data.computeIfPresent(user.getId(), (id, oldUser) -> user);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public User get(int id) {
        readLock.lock();
        try {
            log.info("get {}", id);
            return data.get(id);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<User> getAll() {
        readLock.lock();
        try {
            log.info("getAll");
            return data
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(AbstractNamedEntity::getName))
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public User getByEmail(String email) {
        readLock.lock();
        try {
            log.info("getByEmail {}", email);
            return data.values().stream()
                    .filter(user -> Objects.equals(email, user.getEmail())).findAny().orElse(null);
        } finally {
            readLock.unlock();
        }
    }
}
