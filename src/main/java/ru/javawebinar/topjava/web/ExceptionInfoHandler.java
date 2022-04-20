package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    @Autowired
    private MessageSource messageSource;

    private static Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    //  http://stackoverflow.com/a/22358422/548473
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class, org.springframework.validation.BindException.class})
    public ErrorInfo illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException, ErrorType errorType) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if (logException) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return new ErrorInfo(req.getRequestURL(), getLocalMessage(errorType.name()), getBeautifulMessage(e, errorType, req));
    }

    private String getBeautifulMessage(Throwable rootCause, ErrorType errorType, HttpServletRequest req) {
        String rawMessage = rootCause.toString();
        String message = "";
        switch (errorType) {
            case VALIDATION_ERROR -> {
                if (req.getRequestURI().contains("meals")) {
                    message = rawMessage.contains("NotNull.dateTime") ? message + getLocalMessage("common.dateNotEmpty") : message;
                    message = rawMessage.contains("UniqueDate") ? message + getLocalMessage("common.dateNotDuplicate") : message;
                    boolean isDescriptionBlank = rawMessage.contains("NotBlank.description");
                    message = isDescriptionBlank ? message + getLocalMessage("common.descriptionNotEmpty") : message;
                    message = rawMessage.contains("Size.description") && !isDescriptionBlank ? message + getLocalMessage("common.descriptionSize") : message;
                    boolean isCalorisNull = rawMessage.contains("NotNull.calories");
                    message = isCalorisNull ? message + getLocalMessage("common.caloriesNotEmpty") : message;
                    message = rawMessage.contains("Range.calories") && !isCalorisNull ? message + getLocalMessage("common.caloriesRange") : message;
                }
                if (req.getRequestURI().contains("users")) {
                    boolean isNameBlank = rawMessage.contains("NotBlank.name");
                    message = isNameBlank ? message + getLocalMessage("common.nameNotEmpty") : message;
                    message = rawMessage.contains("Size.name") && !isNameBlank ? message + getLocalMessage("common.nameSize") : message;

                    message = rawMessage.contains("NotBlank.email") ? message + getLocalMessage("common.emailNotEmpty") : message;
                    message = rawMessage.contains("Size.email") ? message + getLocalMessage("common.emailSize") : message;
                    message = rawMessage.contains("Email.userTo.email") ? message + getLocalMessage("common.emailFormat") : message;


                    boolean isPasswordBlank = rawMessage.contains("NotBlank.password");
                    message = isPasswordBlank ? message + getLocalMessage("common.passwordNotEmpty") : message;
                    message = rawMessage.contains("Size.password") && !isPasswordBlank ? message + getLocalMessage("common.passwordSize") : message;
                }
                return message.isEmpty() ? rawMessage : message;
            }
            case DATA_ERROR -> {
                //User message
                message = rootCause.getCause().getCause().getMessage().contains("email_idx") ? message + getLocalMessage("common.emailNotDuplicate") : message;
                //Meal message
                message = rootCause.getCause().getCause().getMessage().contains("datetime_idx") ? message + getLocalMessage("common.dateNotDuplicate") : message;
                return message.isEmpty() ? rawMessage : message;
            }
            case DATA_NOT_FOUND -> {
                return rootCause.getMessage();
            }
            default -> {
                return rawMessage;
            }
        }
    }

    private String getLocalMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault()) + "<br>";
    }
}