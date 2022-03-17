package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private MealService service;

    @GetMapping("/meals/delete/{id}")
    public String deleteMeal(@PathVariable("id") int id) {
        final int userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
        return "redirect:/meals";
    }

    @GetMapping("/meals/update/{id}")
    public String getCreateMenuMeal(Model model, @PathVariable("id") int id) {
        final int userId = SecurityUtil.authUserId();
        model.addAttribute("meal", service.get(id, userId));
        return "mealForm";
    }

    @GetMapping("/meals/create")
    public String getUpdateMenuMeal(Model model) {
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/meals")
    public String getMeals(Model model, HttpServletRequest request) {
        int userId = SecurityUtil.authUserId();
        if ("true".equals(request.getParameter("filter"))) {
            LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
            LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
            LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
            LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
            log.info("getAll filtered for user {}", userId);
            List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
            model.addAttribute("meals",
                    MealsUtil.getFilteredTos(mealsDateFiltered,
                            SecurityUtil.authUserCaloriesPerDay(), startTime, endTime));
            return "meals";
        }
        log.info("getAll for user {}", userId);
        model.addAttribute("meals",
                MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @PostMapping("/meals")
    public String setMeal(Model model, HttpServletRequest request) {
        int userId = SecurityUtil.authUserId();
        final int caloriesPerDay = SecurityUtil.authUserCaloriesPerDay();
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            //assureIdConsistent(meal, getId(request));
            service.update(meal, userId);
        } else {
            checkNew(meal);
            service.create(meal, userId);
        }
        model.addAttribute("meals",
                MealsUtil.getTos(service.getAll(userId), caloriesPerDay));
        return "redirect:/meals";
    }
}
