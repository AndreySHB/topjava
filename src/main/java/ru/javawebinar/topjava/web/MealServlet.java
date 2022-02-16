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
            mealRestController.update(meal, getId(request));
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                mealRestController.delete(id);
                /*if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                    log.info("getAllFilteredAfterDelete");
                    setNeededAttributes(request);
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                    break;
                }*/
                setNeededAttributes(request);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                //response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealRestController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                /*if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                    log.info("getAllFiltered");
                    setNeededAttributes(request);
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                    break;
                }*/
                setNeededAttributes(request);
                log.info("getAll");
                //request.setAttribute("meals", mealRestController.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private void setNeededAttributes(HttpServletRequest request) {
        LocalDate startDate = null;
        try {
            startDate = LocalDate.parse(request.getParameter("startDate"), df);
            startDate = startDate.minusDays(1);
        } catch (Exception ignore) {
        }
        LocalDate endDate = null;
        try {
            endDate = LocalDate.parse(request.getParameter("endDate"), df);
            endDate = endDate.plusDays(1);
        } catch (Exception ignore) {
        }
        LocalTime startTime = null;
        try {
            startTime = LocalTime.parse(request.getParameter("startTime"), tf);
        } catch (Exception ignore) {
        }
        LocalTime endTime = null;
        try {
            endTime = LocalTime.parse(request.getParameter("endTime"), tf);
            endTime = endTime.plusMinutes(1);
        } catch (Exception ignore) {
        }
        request.setAttribute("meals", mealRestController.getFilteredByDateTime(startDate, endDate, startTime, endTime));
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
