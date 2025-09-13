package com.weather.weather_mvp.exception;

import com.weather.weather_mvp.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return new ErrorDto(
                ex.getMessage(),
                "RESOURCE_NOT_FOUND",
                LocalDateTime.now(),
                request.getRequestURI()
        );
    }
}
