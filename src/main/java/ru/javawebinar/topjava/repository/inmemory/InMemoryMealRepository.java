package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> data = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            save(meal, 1);
        }

        for (Meal meal : MealsUtil.meals) {
            Meal mealForUser2 = new Meal(meal.getDateTime(), meal.getDescription() + " forUser2", meal.getCalories());
            save(mealForUser2, 2);
        }
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> userData = getUserData(userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userData.put(meal.getId(), meal);
            return meal;
        }
        if (userData.get(meal.getId()) == null) return null;
        userData.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return getUserData(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        return getUserData(userId).get(id);
    }

    @Override
    public List<Meal> getAll(int userId, Predicate<Meal> filter) {
        return getUserData(userId).values()
                .stream()
                .filter(filter != null ? filter : meal -> true)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Map<Integer, Meal> getUserData(int userId) {
        return data.computeIfAbsent(userId, integer -> new ConcurrentHashMap<>());
    }
}

