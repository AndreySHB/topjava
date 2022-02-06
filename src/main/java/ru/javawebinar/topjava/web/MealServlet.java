package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.Constants;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDataHolder;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final String MEAL_LIST = "/meals.jsp";
    private static final String MEAL_EDIT = "/mealEdit.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null && action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            MealDao.delete(mealId);
        }
        if (action != null && action.equalsIgnoreCase("edit")) {
            getServletContext().getRequestDispatcher(MEAL_EDIT).forward(request, response);
            return;
        }
        request.setAttribute("DTF", Constants.DTF);
        request.setAttribute("dishes", MealsUtil.getMealsTo(MealDataHolder.getData(), Constants.CALORIES_PER_DAY));
        getServletContext().getRequestDispatcher(MEAL_LIST).forward(request, response);
    }
}
