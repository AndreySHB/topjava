package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {
    @Autowired
    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        meal.setUserId(userId);
        return repository.save(meal, userId);
    }

    public boolean delete(int id, int userId) {
        return repository.delete(id, userId);
    }

    public Meal get(int id, int userId) {
        Meal meal = repository.get(id, userId);
        if (meal == null) {
            throw new NotFoundException(String.format("No food with id %d is availible for user with userId = %d", id, userId));
        }
        return meal;
    }

    public List<Meal> getAll(int userId) {
        List<Meal> meals = repository.getAll(userId);
        if (meals.isEmpty()) {
            throw new NotFoundException("No food for user with userId =" + userId);
        }
        return meals;
    }

    public void update(Meal meal, int userId) {
        meal.setUserId(userId);
        checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }
}