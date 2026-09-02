package fr.diginamic.demospring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates application exceptions into HTTP responses for every controller.
 *
 * <ul>
 *   <li>{@link CityException} &rarr; {@code 400 Bad Request}</li>
 *   <li>{@link NotFoundException} &rarr; {@code 404 Not Found}</li>
 *   <li>{@link MethodArgumentNotValidException} (Bean Validation failure on a
 *       {@code @Valid @RequestBody}) &rarr; {@code 400 Bad Request}</li>
 * </ul>
 *
 * <p>Each handler returns the plain message string as the response body.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param ex the business exception
     * @return {@code 400} with the exception message
     */
    @ExceptionHandler(CityException.class)
    public ResponseEntity<String> handleCityException(CityException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * @param ex the not-found exception
     * @return {@code 404} with the exception message
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Reports the first field error raised by Bean Validation.
     *
     * @param ex the validation exception raised by Spring MVC
     * @return {@code 400} with the first violated constraint's message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation error.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
