package ru.job4j.socialmediaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.job4j.socialmediaapi.validation.ValidationErrorResponse;
import ru.job4j.socialmediaapi.validation.Violation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList();
        log.error(e.getLocalizedMessage());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .toList();
        log.error(e.getLocalizedMessage());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    public void catchDataIntegrityViolationException(Exception e,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response)
            throws IOException {
        handleExceptionAndSetResponse(e, request, response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { TransientDataAccessException.class })
    public void catchTransientDataAccessException(Exception e,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws IOException {
        handleExceptionAndSetResponse(e, request, response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = { NoSuchElementException.class })
    public void catchNoSuchElementException(Exception e,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws IOException {
        handleExceptionAndSetResponse(e, request, response, HttpStatus.NOT_FOUND);
    }

    private Map<String, String> convertExceptionToMap(Exception e, HttpServletRequest request) {
        return Map.of(
                "message", e.getMessage(),
                "type", String.valueOf(e.getClass()),
                "timestamp", String.valueOf(LocalDateTime.now()),
                "path", request.getRequestURI());
    }

    private void handleExceptionAndSetResponse(Exception e,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               HttpStatusCode status) throws IOException {
        Map<String, String> details = convertExceptionToMap(e, request);
        response.setStatus(status.value());
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(details));
        log.error(e.getLocalizedMessage());
    }
}
