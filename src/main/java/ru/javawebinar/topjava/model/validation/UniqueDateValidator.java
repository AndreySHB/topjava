package ru.javawebinar.topjava.model.validation;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class UniqueDateValidator implements ConstraintValidator<UniqueDate, LocalDateTime> {

    @Autowired
    private MealRepository repository;

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext context) {
        AuthorizedUser auth = SecurityUtil.safeGet();
        return auth == null || repository.getAll(auth.getId()).stream().map(Meal::getDateTime).noneMatch(dt->dt.equals(dateTime));
    }
}
