package com.example.controller;

import com.example.aspect.LogExecutionTime;
import com.example.exception.DateBoundsException;
import com.example.exception.InvalidCurrencyException;
import com.example.exception.ServiceUnavailableException;
import com.example.exception.entity.EntityNotFoundException;
import com.example.exception.entity.RelatedEntityNotFoundException;
import com.example.exception.entity.SlugAlreadyExistsException;
import com.example.exception.entity.UserAlreadyRegisterException;
import com.example.exception.entity.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;
import java.util.Objects;

@Slf4j
@LogExecutionTime
@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException exception, Locale locale) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("errors.400.title", new Object[0],
                                "errors.400.title", locale));

        problemDetail.setProperty("errors",
                exception.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .toList());

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception, Locale locale) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        Objects.requireNonNull(
                                        messageSource.getMessage("request.param.not_specified", new Object[0],
                                                "request.param.not_specified", locale))
                                .formatted(exception.getParameterName(), exception.getParameterType()));

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(
            ConstraintViolationException exception, Locale locale) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("errors.400.title", new Object[0],
                                "errors.400.title", locale));

        problemDetail.setProperty("errors", exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList());

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(DateBoundsException.class)
    public ResponseEntity<ProblemDetail> handleDateBoundsException(DateBoundsException exception, Locale locale) {
        return ResponseEntity.badRequest()
                .body(ProblemDetail
                        .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                messageSource.getMessage(exception.getMessage(), new Object[0],
                                        exception.getMessage(), locale)));
    }

    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCurrencyException(InvalidCurrencyException exception,
                                                                        Locale locale) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        Objects.requireNonNull(messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale)).formatted(exception.getCurrency()));

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCurrencyException(ServiceUnavailableException exception,
                                                                        Locale locale) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ProblemDetail
                        .forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                                messageSource.getMessage(exception.getMessage(), new Object[0],
                                        exception.getMessage(), locale)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            Locale locale) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail
                        .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                messageSource.getMessage(exception.getMessage(), new Object[0],
                                        exception.getMessage(), locale)));
    }

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleSlugAlreadyExistsException(SlugAlreadyExistsException exception,
                                                                          Locale locale) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                        messageSource.getMessage(exception.getMessage(), new Object[]{exception.getSlug()},
                                exception.getMessage(), locale)));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException exception,
                                                                       Locale locale) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                        messageSource.getMessage(exception.getMessage(), new Object[]{exception.getId()},
                                exception.getMessage(), locale)));
    }

    @ExceptionHandler(RelatedEntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleRelatedEntityNotFoundException(RelatedEntityNotFoundException exception,
                                                                              Locale locale) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage(exception.getMessage(), new Object[]{exception.getId()},
                                exception.getMessage(), locale)));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFoundException(@NonNull UserNotFoundException exception,
                                                                     Locale locale) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage(exception.getMessage(), new Object[]{exception.getLogin()},
                                exception.getMessage(), locale)));
    }

    @ExceptionHandler(UserAlreadyRegisterException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyRegisterException(@NonNull UserAlreadyRegisterException exception,
                                                                            Locale locale) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage(exception.getMessage(), new Object[]{exception.getLogin()},
                                exception.getMessage(), locale)));
    }
}
