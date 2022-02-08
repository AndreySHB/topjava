package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    List<Meal> getAll();

    Meal getMeal(int id);

    boolean removeMeal(int id);

    Meal editMeal(int id, Meal meal);

    Meal addMeal(Meal meal);
}
