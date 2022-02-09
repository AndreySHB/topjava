package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    List<Meal> getAll();

    Meal get(int id);

    Meal remove(int id);

    Meal edit(int id, Meal meal);

    void add(Meal meal);
}
