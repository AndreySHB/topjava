package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MealService service;

    public List<MealTo> getAll(int userId) {
        log.info("getAll");
        if (userId == SecurityUtil.authUserId()) {
            return MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY);
        }
        return Collections.emptyList();
    }

    public List<MealTo> getAll(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAll");
        if (userId == SecurityUtil.authUserId()) {
            return MealsUtil.filterByPredicate(service.getAll(SecurityUtil.authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY,
                    meal1 -> meal1.getDate().isAfter(startDate) && meal1.getDate().isBefore(endDate) && DateTimeUtil.isBetweenHalfOpen(meal1.getTime(), startTime, endTime));
        }
        return Collections.emptyList();
    }

    public void create(Meal meal) {
        log.info("create");
        service.create(meal, SecurityUtil.authUserId());
    }

    public void update(Meal meal) {
        log.info("update");
        service.update(meal, SecurityUtil.authUserId());
    }

    public Meal get(int id, int userId) {
        log.info("get");
        if (SecurityUtil.authUserId() == userId) {
            return service.get(id, SecurityUtil.authUserId());
        }
        return null;
    }

    public boolean delete(int id, int userId) {
        log.info("delete");
        if (userId == SecurityUtil.authUserId()) {
            return service.delete(id, SecurityUtil.authUserId());
        }
        return false;
    }
}