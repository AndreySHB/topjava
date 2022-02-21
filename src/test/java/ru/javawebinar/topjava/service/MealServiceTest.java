package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;


@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(USERMEAL_ID, USER_ID);
        assertMatch(meal, userMeal);
    }

    @Test
    public void getExistedForWrongUser() {
        assertThrows(NotFoundException.class, () -> service.get(USERMEAL_ID, ADMIN_ID));
    }

    @Test
    public void getNotExisted() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_EXISTED, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(USERMEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USERMEAL_ID, USER_ID));
    }

    @Test
    public void deleteForWrongUser() {
        assertThrows(NotFoundException.class, () -> service.delete(USERMEAL_ID, ADMIN_ID));
    }

    @Test
    public void deleteNotExisted() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_EXISTED, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = service.getBetweenInclusive(DATE.toLocalDate(), DATE.toLocalDate(), USER_ID);
        assertMatch(meals, userMeal);
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(USER_ID);
        assertMatch(meals, userMeal3, userMeal2, userMeal);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(getUpdated(), USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), getUpdated());
    }

    @Test
    public void updateForeignFood() {
        Meal updated = getUpdated();
        updated.setId(FOREIGN_FOOD_ID);
        assertThrows(NotFoundException.class, () -> service.update(updated, USER_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createNotUniqDate() {
        Meal newMeal = getNew();
        newMeal.setDateTime(DATE);
        assertThrows(DataAccessException.class, () -> service.create(newMeal, USER_ID));
    }
}