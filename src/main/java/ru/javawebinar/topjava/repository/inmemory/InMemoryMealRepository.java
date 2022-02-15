package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> data = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            meal.setUserId(1);
            save(meal);
        }

        for (Meal meal : MealsUtil.meals) {
            Meal meal1 = new Meal(meal.getDateTime(), meal.getDescription() + " forUser2", meal.getCalories());
            meal1.setUserId(2);
            save(meal1);
        }
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            data.put(meal.getId(), meal);
            return meal;
        }
        Meal oldValue = data.get(meal.getId());
        return (oldValue != null && oldValue.getUserId() == meal.getUserId()) ? data.put(meal.getId(), meal) : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = data.get(id);
        return meal != null && meal.getUserId() == userId && data.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = data.get(id);
        return (meal != null && meal.getUserId() == userId) ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return data.values()
                .stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getFilteredByDate(int userId, LocalDate startDate, LocalDate endDate) {
        return data.values()
                .stream()
                .filter(meal -> (meal.getUserId() == userId) && (meal.getDate().isAfter(startDate) && meal.getDate().isBefore(endDate)))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

