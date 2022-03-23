package ru.javawebinar.topjava.web.meal;

import org.springframework.lang.Nullable;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MealController {
    void delete (int id);

    List<MealTo> getAll();

    Meal get(int id);

    Meal getNew();

    Meal create(Meal meal);

    void update(Meal meal, int id);

    List<MealTo> getBetween(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                            @Nullable LocalDate endDate, @Nullable LocalTime endTime);
}
