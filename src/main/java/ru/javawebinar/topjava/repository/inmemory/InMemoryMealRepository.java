package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> data = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    {
        for (Meal meal : MealsUtil.meals) {
            meal.setUserId(1);
            save(meal, 1);
        }
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.getUserId() == userId) {
            try {
                writeLock.lock();
                if (meal.isNew()) {
                    meal.setId(counter.incrementAndGet());
                    data.put(meal.getId(), meal);
                    return meal;
                }
                return data.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
            } finally {
                writeLock.unlock();
            }
        }
        return null;
    }

    @Override
    public boolean delete(int id, int userId) {
        writeLock.lock();
        try {
            return data.get(id) != null && data.get(id).getUserId() == userId && data.remove(id) != null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Meal get(int id, int userId) {
        readLock.lock();
        try {
            Meal meal = data.get(id);
            return (meal != null && meal.getUserId() == userId) ? meal : null;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Meal> getAll(int userId) {
        readLock.lock();
        try {
            return data.values().stream().filter(meal -> meal.getUserId() == userId)
                    .sorted((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime())).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }
}

