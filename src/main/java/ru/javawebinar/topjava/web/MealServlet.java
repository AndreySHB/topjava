package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MealServlet extends HttpServlet {
    private static final String MEAL_JSP = "/meals.jsp";
    private static final String MEAL_EDIT = "/mealEdit.jsp";
    private static final String MEAL_ADD = "/mealAdd.jsp";
    private static final String MEAL_SERVLET = "/meals";
    public static final int CALORIES_PER_DAY = 2000;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final MealDao mealDao = new InMemoryMealDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");
        action = action == null ? "" : action;
        if (action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            mealDao.removeMeal(mealId);
            response.sendRedirect(getServletContext().getContextPath() + MEAL_SERVLET);
            return;
        }
        if (action.equalsIgnoreCase("edit")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            request.setAttribute("meal", mealDao.getMeal(mealId));
            getServletContext().getRequestDispatcher(MEAL_EDIT).forward(request, response);
            return;
        }
        if (action.equalsIgnoreCase("add")) {
            getServletContext().getRequestDispatcher(MEAL_ADD).forward(request, response);
            return;
        }
        request.setAttribute("FORMATTER", DATE_TIME_FORMATTER);
        request.setAttribute("meals", MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
        getServletContext().getRequestDispatcher(MEAL_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime localDateTime = LocalDateTime.now();
        try {
            localDateTime = LocalDateTime.parse(request.getParameter("datetime"));
        } catch (DateTimeParseException ignore) {
        }
        String description = request.getParameter("description");
        int calories = 0;
        try {
            calories = Math.max(0, Integer.parseInt(request.getParameter("calories")));
        } catch (NumberFormatException ignore) {
        }
        Meal meal = new Meal(localDateTime, description, calories);
        if (request.getParameter("id") == null) {
            mealDao.addMeal(meal);
        } else {
            int id = Integer.parseInt(request.getParameter("id"));
            mealDao.editMeal(id, meal);
        }
        response.sendRedirect(getServletContext().getContextPath() + MEAL_SERVLET);
    }
}
