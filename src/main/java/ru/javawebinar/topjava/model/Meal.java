package ru.javawebinar.topjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Meal extends AbstractBaseEntity {

    private LocalDateTime dateTime;

    private String description;

    private int calories;

    public Meal() {
    }

    public Meal(Meal m) {
        this(m.id, m.dateTime, m.description, m.calories);
    }

    public Meal(LocalDateTime date, String description, int calories) {
        this(null, date, description, calories);
    }

    public Meal(Integer id, LocalDateTime date, String description, int calories) {
        super(id);
        this.dateTime = date;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public LocalDate getOnlyDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getOnlyTime() {
        return dateTime.toLocalTime();
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
