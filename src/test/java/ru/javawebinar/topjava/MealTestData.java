package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int USERMEAL_ID = START_SEQ + 3;
    public static final int USERMEAL_ID2 = USERMEAL_ID + 1;
    public static final int USERMEAL_ID3 = USERMEAL_ID2 + 1;
    public static final int NOT_EXISTED = 55;
    public static final int FOREIGN_FOOD_ID = START_SEQ + 6;
    public static final LocalDateTime DATE = DateTimeUtil.parseDate("2022-05-16 10:04");
    public static final LocalDateTime DATE2 = DateTimeUtil.parseDate("2022-05-17 15:04");
    public static final LocalDateTime DATE3 = DateTimeUtil.parseDate("2022-05-17 19:04");
    public static final LocalDateTime NEW_DATE = DateTimeUtil.parseDate("2022-12-12 12:12");
    public static final Meal userMeal = new Meal(USERMEAL_ID, DATE, "макароны с сыром", 1200);
    public static final Meal userMeal2 = new Meal(USERMEAL_ID2, DATE2, "печенка с ананасом", 795);
    public static final Meal userMeal3 = new Meal(USERMEAL_ID3, DATE3, "жаренный круассан", 500);

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal);
        updated.setDateTime(DATE.plusDays(4));
        updated.setCalories(100);
        updated.setDescription("макароны с сыром updated");
        return updated;
    }

    public static Meal getNew() {
        return new Meal(null, NEW_DATE, "new", 100500);
    }
}
