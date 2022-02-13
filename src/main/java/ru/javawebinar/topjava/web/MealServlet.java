package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

    private MealRestController mealRestController;

    @Override
    public void init() {
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            mealRestController = appCtx.getBean(MealRestController.class);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        if (meal.isNew()) {
            mealRestController.create(meal);
        } else {
            mealRestController.update(meal);
        }
        response.sendRedirect("meals?userId=" + getUserId(request));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                mealRestController.delete(id, getUserId(request));
                if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                    String s = request.getParameter("startDate");
                    log.info("getAllFilteredAfterDelete");
                    setDateTimeAttributes(request);
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                    break;
                }
                response.sendRedirect("meals?userId=" + getUserId(request));
                break;
            case "create":
            case "update":
                request.setAttribute("userId", getUserId(request));
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealRestController.get(getId(request), getUserId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                request.setAttribute("userId", getUserId(request));
                if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                    log.info("getAllFiltered");
                    setDateTimeAttributes(request);
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                    break;
                }
                log.info("getAll");
                request.setAttribute("meals", mealRestController.getAll(getUserId(request)));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private void setDateTimeAttributes(HttpServletRequest request) {
        LocalDate startDate = LocalDate.parse(request.getParameter("startDate"), df);
        LocalDate endDate = LocalDate.parse(request.getParameter("endDate"), df);
        LocalTime startTime = LocalTime.parse(request.getParameter("startTime"), tf);
        LocalTime endTime = LocalTime.parse(request.getParameter("endTime"), tf);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("startTime", startTime);
        request.setAttribute("endTime", endTime);
        request.setAttribute("meals", mealRestController.getAll(getUserId(request), startDate.minusDays(1), endDate.plusDays(1), startTime, endTime.plusMinutes(1)));
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private int getUserId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("userId"));
        return Integer.parseInt(paramId);
    }
}
