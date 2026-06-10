package com.moviesapi.movies_api.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Not Found", "message", ex.getMessage()));
    }

    @ExceptionHandler(RelationshipConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(RelationshipConflictException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Conflict", "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldErrorMap)
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation Failed");
        body.put("fields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadable(HttpMessageNotReadableException ex) {
        String message = "Malformed request body";
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof InvalidFormatException ife) {
                String fieldName = ife.getPath().isEmpty() ? "unknown" : ife.getPath().get(0).getFieldName();
                message = "Invalid value for field '" + fieldName + "': expected correct format (e.g. dates as YYYY-MM-DD)";
                break;
            }
            if (cause instanceof java.time.format.DateTimeParseException) {
                message = "Invalid date format: expected YYYY-MM-DD";
                break;
            }
            cause = cause.getCause();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request", "message", message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> fieldErrors = ex.getConstraintViolations().stream()
                .map(v -> {
                    String param = v.getPropertyPath().toString();
                    String name = param.contains(".") ? param.substring(param.lastIndexOf('.') + 1) : param;
                    return Map.of("field", name, "message", v.getMessage());
                })
                .collect(Collectors.toList());

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation Failed");
        body.put("fields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request", "message", ex.getMessage()));
    }

    private Map<String, String> toFieldErrorMap(FieldError fe) {
        return Map.of("field", fe.getField(), "message", fe.getDefaultMessage());
    }
}
