package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.trace("getAll");
        return MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getFilteredByDateTime(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.trace("getAll");
        return MealsUtil.filterByPredicate(service.getFilteredByDate(SecurityUtil.authUserId(), startDate, endDate), SecurityUtil.authUserCaloriesPerDay(),
                meal1 -> DateTimeUtil.isBetweenHalfOpen(meal1.getTime(), startTime, endTime));
    }

    public void create(Meal meal) {
        log.trace("create");
        checkNew(meal);
        service.create(meal, SecurityUtil.authUserId());
    }

    public void update(Meal meal, int id) {
        log.trace("update");
        assureIdConsistent(meal, id);
        service.update(meal, SecurityUtil.authUserId());
    }

    public Meal get(int id) {
        log.trace("get");
        return service.get(id, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        log.trace("delete");
        service.delete(id, SecurityUtil.authUserId());
    }
}