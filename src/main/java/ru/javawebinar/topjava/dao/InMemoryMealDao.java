package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryMealDao implements MealDao {
    private final List<Meal> list = new CopyOnWriteArrayList<>();

    public InMemoryMealDao() {
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public List<Meal> getAll() {
        List<Meal> meals = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Meal meal = list.get(i);
            if (meal != null) {
                meals.add(new Meal(i + 1, meal.getDateTime(), meal.getDescription(), meal.getCalories()));
            }
        }
        return meals;
    }

    @Override
    public Meal get(int id) {
        Meal meal = list.get(id - 1);
        return new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
    }

    @Override
    public Meal remove(int id) {
        return list.set(id - 1, null);
    }

    @Override
    public Meal edit(int id, Meal meal) {
        return list.set(id - 1, meal);
    }

    @Override
    public void add(Meal meal) {
        list.add(meal);
    }
}
