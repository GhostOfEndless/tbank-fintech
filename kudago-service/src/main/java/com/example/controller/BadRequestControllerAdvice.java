package com.example.controller;

import com.example.aspect.LogExecutionTime;
import com.example.exception.CurrencyServiceUnavailableException;
import com.example.exception.DateBoundsException;
import com.example.exception.InvalidCurrencyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
                        messageSource.getMessage("errors.400.title", new Object[0],
                                "errors.400.title", locale));

        problemDetail.setProperty("errors", Objects.requireNonNull(
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
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("errors.400.title", new Object[0],
                                "errors.400.title", locale));

        problemDetail.setProperty("error", messageSource.getMessage(exception.getMessage(), new Object[0],
                exception.getMessage(), locale));

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCurrencyException(InvalidCurrencyException exception,
                                                                        Locale locale) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("errors.400.title", new Object[0],
                                "errors.400.title", locale));

        problemDetail.setProperty("error", Objects.requireNonNull(
                        messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale)).formatted(exception.getCurrency()));

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(CurrencyServiceUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCurrencyException(CurrencyServiceUnavailableException exception,
                                                                        Locale locale) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                        messageSource.getMessage("errors.503.title", new Object[0],
                                "errors.503.title", locale));

        problemDetail.setProperty("error", messageSource.getMessage(exception.getMessage(), new Object[0],
                        exception.getMessage(), locale));

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(problemDetail);
    }
}
