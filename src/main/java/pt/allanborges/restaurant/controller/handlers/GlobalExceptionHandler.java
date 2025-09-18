package pt.allanborges.restaurant.controller.handlers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pt.allanborges.restaurant.controller.handlers.exceptions.NoSuchElementException;
import pt.allanborges.restaurant.controller.handlers.exceptions.ResourceNotFoundException;
import pt.allanborges.restaurant.controller.handlers.exceptions.StandardError;
import pt.allanborges.restaurant.controller.handlers.exceptions.ValidationEx;

import java.util.ArrayList;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Log4j2
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<StandardError> handleResourceNotFoundException(final ResourceNotFoundException ex, final HttpServletRequest request) {
        return ResponseEntity.status(NOT_FOUND).body(
                StandardError.builder()
                        .timestamp(now())
                        .status(NOT_FOUND.value())
                        .error(NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<StandardError> handleNoSuchElementException(final NoSuchElementException ex, final HttpServletRequest request) {
        return ResponseEntity.status(NOT_FOUND).body(
                StandardError.builder()
                        .timestamp(now())
                        .status(NOT_FOUND.value())
                        .error(NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<StandardError> handleIllegalArgumentException(final IllegalArgumentException ex, final HttpServletRequest request) {
        return ResponseEntity.status(BAD_REQUEST).body(
                StandardError.builder()
                        .timestamp(now())
                        .status(BAD_REQUEST.value())
                        .error(BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<StandardError> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        var error = ValidationEx.builder()
                .timestamp(now())
                .status(BAD_REQUEST.value())
                .error("Validation Exception")
                .message("Exception in validation attributes")
                .path(request.getRequestURI())
                .errors(new ArrayList<>())
                .build();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(error);
    }


    // --- AUTH / SECURITY ----------------------------------------------------

    // Invalid credentials, unknown user, etc. -> 401
    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            AuthenticationException.class
    })
    public ResponseEntity<StandardError> handleAuthExceptions(Exception ex, HttpServletRequest request) {
        String msg = "Invalid username or password";

        if (ex instanceof AccountStatusException)
            msg = "Authentication failed";

        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(std(HttpStatus.UNAUTHORIZED, msg, request));
    }

    // Disabled, locked, expired credentials, etc. -> 403
    @ExceptionHandler({
            DisabledException.class,
            LockedException.class,
            AccountExpiredException.class,
            CredentialsExpiredException.class,
            AccountStatusException.class, // umbrella
            AccessDeniedException.class
    })
    public ResponseEntity<StandardError> handleAccessDenied(Exception ex, HttpServletRequest request) {
        String msg = (ex.getMessage() != null && !ex.getMessage().isBlank()) ? ex.getMessage() : "Access is denied";
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(std(HttpStatus.FORBIDDEN, msg, request));
    }

    // Catch-all (optional, good for logging)
    @ExceptionHandler(Exception.class)
    ResponseEntity<StandardError> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(std(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request));
    }

    //helper for Auth exceptions
    private StandardError std(HttpStatus status, String message, HttpServletRequest req) {
        return StandardError.builder()
                .timestamp(now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(req.getRequestURI())
                .build();
    }

}