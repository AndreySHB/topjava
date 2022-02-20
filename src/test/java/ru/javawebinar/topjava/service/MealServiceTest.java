package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Date;
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
    }

    @Test
    public void update() {
    }

    @Test
    public void create() {
    }
}