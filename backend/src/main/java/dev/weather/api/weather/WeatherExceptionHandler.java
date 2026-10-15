package dev.weather.api.weather;

import java.util.Map;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WeatherExceptionHandler {

    @ExceptionHandler(WeatherProviderException.class)
    public ResponseEntity<Map<String, String>> handleProviderFailure(WeatherProviderException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(ConstraintViolationException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "The request contains invalid weather search parameters."));
    }
}
