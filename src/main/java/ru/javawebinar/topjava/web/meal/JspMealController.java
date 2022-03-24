package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController {

    @GetMapping("/delete/{id}")
    public String deleteMeal(@PathVariable("id") int id) {
        delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/update/{id}")
    public String getUpdate(Model model, @PathVariable("id") int id) {
        model.addAttribute("meal", get(id));
        return "mealForm";
    }

    @GetMapping("/create")
    public String getCreate(Model model) {
        model.addAttribute("meal", getNew());
        return "mealForm";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }

    @GetMapping("/filter")
    public String getAllFiltered(Model model, HttpServletRequest request) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        final List<MealTo> between = getBetween(startDate, startTime, endDate, endTime);
        model.addAttribute("meals", between);
        return "meals";
    }

    @PostMapping
    public String save(HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        String sId = request.getParameter("id");
        if (StringUtils.hasLength(sId)) {
            int id = Integer.parseInt(sId);
            update(meal, id);
        } else {
            create(meal);
        }
        return "redirect:/meals";
    }
}
