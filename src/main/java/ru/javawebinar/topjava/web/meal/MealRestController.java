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
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getFilteredByDateTime(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAll");
        startDate = startDate != null ? startDate : LocalDate.of(1990, 1, 1);
        endDate = endDate != null ? endDate : LocalDate.of(2990, 1, 1);
        LocalTime finalStartTime = startTime != null ? startTime : LocalTime.of(0, 0);
        LocalTime finalEndTime = endTime != null ? endTime : LocalTime.of(23, 59);
        return MealsUtil.filterByPredicate(service.getFilteredByDate(SecurityUtil.authUserId(), startDate, endDate), SecurityUtil.authUserCaloriesPerDay(),
                meal1 -> DateTimeUtil.isBetweenHalfOpen(meal1.getTime(), finalStartTime, finalEndTime));
    }

    public void create(Meal meal) {
        log.info("create");
        checkNew(meal);
        service.create(meal, SecurityUtil.authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update");
        assureIdConsistent(meal, id);
        service.update(meal, SecurityUtil.authUserId());
    }

    public Meal get(int id) {
        log.info("get");
        return service.get(id, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        log.info("delete");
        service.delete(id, SecurityUtil.authUserId());
    }
}