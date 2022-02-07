package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    List<Meal> getData();

    Meal getMeal(int mealId);

    boolean removeMeal(int mealId);

    boolean editMeal(int mealId, Meal meal);

    boolean addMeal(Meal meal);
}
