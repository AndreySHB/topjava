package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.Constants;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public class MealServlet extends HttpServlet {
    private static final String MEAL_JSP = "/meals.jsp";
    private static final String MEAL_EDIT = "/mealEdit.jsp";
    private static final String MEAL_ADD = "/mealAdd.jsp";
    private static final String MEAL_SERVLET = "/meals";
    private static final MealDao mealDao = new InMemoryMealDao();

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
        request.setAttribute("FORMATTER", Constants.DATE_TIME_FORMATTER);
        List<MealTo> mealTos = MealsUtil.filteredByStreams(mealDao.getData(), LocalTime.MIN, LocalTime.MAX, Constants.CALORIES_PER_DAY);
        mealTos.sort(new Comparator<MealTo>() {
            @Override
            public int compare(MealTo o1, MealTo o2) {
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });
        request.setAttribute("meals", mealTos);
        getServletContext().getRequestDispatcher(MEAL_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String sId = request.getParameter("id") == null ? "-999" : request.getParameter("id");
        int id = Integer.parseInt(sId);
        LocalDateTime localDateTime = LocalDateTime.parse(request.getParameter("datetime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        Meal meal = new Meal(localDateTime, description, calories);
        if (id == -999) {
            mealDao.addMeal(meal);
        } else {
            mealDao.editMeal(id, meal);
        }
        response.sendRedirect(getServletContext().getContextPath() + MEAL_SERVLET);
    }
}
