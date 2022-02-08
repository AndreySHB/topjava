package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealDao implements MealDao {
    private final Object lock = new Object();
    private final Object value = new Object();
    private final AtomicInteger id = new AtomicInteger(0);
    private final ConcurrentHashMap<Meal, Object> data = new ConcurrentHashMap<>();

    public InMemoryMealDao() {
        data.put(new Meal(id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500), value);
        data.put(new Meal(id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000), value);
        data.put(new Meal(id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500), value);
        data.put(new Meal(id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100), value);
        data.put(new Meal(id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000), value);
        data.put(new Meal(id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500), value);
        data.put(new Meal(id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410), value);
    }


    @Override
    public List<Meal> getAll() {
        List<Meal> meals = new ArrayList<>();
        for (Meal meal : data.keySet()) {
            meals.add(new Meal(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories()));
        }
        return meals;
    }

    @Override
    public Meal getMeal(int id) {
        Meal meal = data.keySet().stream().filter(e -> e.getId() == id).findAny().orElse(null);
        if (meal == null) return null;
        return new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
    }

    @Override
    public boolean removeMeal(int id) {
        Meal meal = data.keySet().stream().filter(e -> e.getId() == id).findAny().orElse(null);
        if (meal == null) return false;
        return data.remove(meal) == value;
    }

    @Override
    public Meal editMeal(int id, Meal meal) {
        removeMeal(id);
        synchronized (lock) {
            meal.setId(id);
            data.put(meal, value);
        }
        return meal;
    }

    @Override
    public Meal addMeal(Meal meal) {
        synchronized (lock) {
            data.put(meal, value);
            meal.setId(id.incrementAndGet());
        }
        return meal;
    }
}
