package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

public class MealDao {
    public static void delete(int mealId) {
        Meal meal = MealDataHolder.getData().stream().filter(e -> e.getId() == mealId).findAny().get();
        MealDataHolder.getData().remove(meal);
    }
}
