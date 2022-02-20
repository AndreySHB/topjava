package ru.javawebinar.topjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Meal extends AbstractBaseEntity {

    public Meal() {
    }

    private LocalDateTime date;

    private String description;

    private int calories;

    public Meal(LocalDateTime date, String description, int calories) {
        this(null, date, description, calories);
    }

    public Meal(Integer id, LocalDateTime date, String description, int calories) {
        super(id);
        this.date = date;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public LocalDate getOnlyDate() {
        return date.toLocalDate();
    }

    public LocalTime getOnlyTime() {
        return date.toLocalTime();
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + date +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
