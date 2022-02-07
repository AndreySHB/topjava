package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealDao implements MealDao {
    private static final AtomicInteger Id = new AtomicInteger(0);
    private static final List<Meal> data = Collections.synchronizedList(new ArrayList<>());

    static {
        data.add(new Meal(Id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        data.add(new Meal(Id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        data.add(new Meal(Id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        data.add(new Meal(Id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        data.add(new Meal(Id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        data.add(new Meal(Id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        data.add(new Meal(Id.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public List<Meal> getData() {
        return data;
    }

    @Override
    public Meal getMeal(int mealId) {
        return data.stream().filter(e -> e.getId() == mealId).findAny().get();
    }

    @Override
    public boolean removeMeal(int mealId) {
        Meal meal = null;
        try {
            meal = data.stream().filter(e -> e.getId() == mealId).findAny().get();
        } catch (NoSuchElementException e) {
            return false;
        }
        return data.remove(meal);
    }

    @Override
    public boolean editMeal(int mealId, Meal meal) {
        if (removeMeal(mealId)) {
            meal.setId(mealId);
            return data.add(meal);
        }
        return false;
    }

    @Override
    public boolean addMeal(Meal meal) {
        boolean added = data.add(meal);
        meal.setId(Id.incrementAndGet());
        return added;
    }
}
