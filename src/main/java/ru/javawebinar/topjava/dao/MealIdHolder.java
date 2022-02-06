package ru.javawebinar.topjava.dao;

import java.util.concurrent.atomic.AtomicInteger;

public class MealIdHolder {
    private static final AtomicInteger Id = new AtomicInteger(0);

    public static int getId() {
        return Id.incrementAndGet();
    }
}
