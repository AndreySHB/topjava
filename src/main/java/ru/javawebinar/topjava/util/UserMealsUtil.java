package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        Map<LocalDate, Integer> caloriesPerDays = getCaloriesPerDays(meals);
        List<UserMealWithExcess> userMealWithExcesses = new ArrayList<>();
        LocalTime localTime;
        LocalDate localDate;
        for (UserMeal meal : meals) {
            localTime = meal.getDateTime().toLocalTime();
            localDate = meal.getDateTime().toLocalDate();
            if (TimeUtil.isBetweenHalfOpen(localTime, startTime, endTime)) {
                UserMealWithExcess mealWithExcess = new UserMealWithExcess(
                        meal.getDateTime(), meal.getDescription(), meal.getCalories(), caloriesPerDays.get(localDate) > caloriesPerDay);
                userMealWithExcesses.add(mealWithExcess);
            }
        }
        return userMealWithExcesses;
    }

    private static Map<LocalDate, Integer> getCaloriesPerDays(List<UserMeal> meals) {
        Map<LocalDate, Integer> caloriesPerDays = new HashMap<>();
        for (UserMeal meal : meals) {
            Integer callories = meal.getCalories();
            LocalDate localDate = meal.getDateTime().toLocalDate();
            Integer value = caloriesPerDays.get(localDate) == null ? 0 : caloriesPerDays.get(localDate);
            caloriesPerDays.put(localDate, value + callories);

        }
        return caloriesPerDays;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        System.out.println("test");
        return null;
    }
}
