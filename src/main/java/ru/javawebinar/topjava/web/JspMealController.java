package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private MealService service;

    @GetMapping("/meals")
    public String getMeals(Model model, HttpServletRequest request) {
        String action = request.getParameter("action");
        final int caloriesPerDay = SecurityUtil.authUserCaloriesPerDay();
        final int userId = SecurityUtil.authUserId();
        switch (action == null ? "all" : action) {
            case "create" -> {
                final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
                log.info("create meal {} for user {}", getId(request), userId);
                model.addAttribute("meal", meal);
                return "mealForm";
            }
            case "update" -> {
                final Meal meal = service.get(getId(request), userId);
                log.info("update meal {} for user {}", getId(request), userId);
                model.addAttribute("meal", meal);
                return "mealForm";
            }
            case "delete" -> {
                log.info("delete meal {} for user {}", getId(request), userId);
                service.delete(getId(request), userId);
                model.addAttribute("meals",
                        MealsUtil.getTos(service.getAll(userId), caloriesPerDay));
                return "meals";
            }
            case "filter" -> {
                LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
                LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
                LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
                LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
                List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
                model.addAttribute("meals",
                        MealsUtil.getFilteredTos(mealsDateFiltered, caloriesPerDay,
                                startTime, endTime));
                return "meals";
            }
            default -> {
                log.info("getAll for user {}", userId);
                model.addAttribute("meals",
                        MealsUtil.getTos(service.getAll(userId), caloriesPerDay));
                return "meals";
            }
        }
    }

    @PostMapping("/meals")
    public String setMeals(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        int userId = SecurityUtil.authUserId();
        final int caloriesPerDay = SecurityUtil.authUserCaloriesPerDay();
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            assureIdConsistent(meal, getId(request));
            log.info("update {} for user {}", meal, userId);
            service.update(meal, userId);
        } else {
            checkNew(meal);
            log.info("create {} for user {}", meal, userId);
            service.create(meal, userId);
        }
        model.addAttribute("meals",
                MealsUtil.getTos(service.getAll(userId), caloriesPerDay));
        return "meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
