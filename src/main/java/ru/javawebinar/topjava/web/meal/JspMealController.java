package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Validated
@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController {

    @GetMapping("/delete/{id}")
    public String deleteMeal(@PathVariable("id") int id) {
        final int userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
        return "redirect:/meals";
    }

    @GetMapping("/update/{id}")
    public String getCreateMenuMeal(Model model, @PathVariable("id") int id) {
        final int userId = SecurityUtil.authUserId();
        log.info("goto update menu for meal {} user {}", id, userId);
        model.addAttribute("meal", service.get(id, userId));
        return "mealForm";
    }

    @GetMapping("/create")
    public String getUpdateMenuMeal(Model model) {
        log.info("goto create menu for user {}", SecurityUtil.authUserId());
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping()
    public String getMeals(Model model) {
        int userId = SecurityUtil.authUserId();
        log.info("getAll for user {}", userId);
        putMealTos(model, MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping("/filter")
    public String getFilteredMeals(Model model, HttpServletRequest request) {
        int userId = SecurityUtil.authUserId();
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        log.info("getAll filtered for user {}", userId);
        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
        putMealTos(model, MealsUtil.getFilteredTos(mealsDateFiltered,
                SecurityUtil.authUserCaloriesPerDay(), startTime, endTime));
        return "meals";
    }

    @PostMapping("/save")
    public String save(HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        int userId = SecurityUtil.authUserId();
        String s = request.getParameter("id");
        if (s != null && !s.isEmpty()) {
            int id = Integer.parseInt(s);
            meal.setId(Integer.parseInt(s));
            log.info("update meal {} for user {}", id, userId);
            service.update(meal, userId);
        } else {
            log.info("create new meal for user {}", userId);
            service.create(meal, userId);
        }
        return "redirect:/meals";
    }

    private void putMealTos(Model model, List<MealTo> meals) {
        model.addAttribute("meals", meals);
    }
}
