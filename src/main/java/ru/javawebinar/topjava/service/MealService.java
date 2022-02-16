package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        return repository.save(meal, userId);
    }

    public void delete(int id, int userId) {
        checkNotFoundWithId(repository.delete(id, userId), id);
    }

    public Meal get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public List<Meal> getAll(int userId) {
        return repository.getAll(userId, null);
    }

    public List<Meal> getFilteredByDate(int userId, LocalDate startDate, LocalDate endDate) {
        return repository.getAll(userId, meal -> DateTimeUtil.isBetweenDates(meal.getDate(), startDate, endDate));
    }

    public void update(Meal meal, int userId) {
        checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }
}