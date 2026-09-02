package fr.diginamic.demospring.exception;

/**
 * Checked exception thrown when a requested resource (city or department) does
 * not exist.
 *
 * <p>Mapped to HTTP {@code 404 Not Found} by {@link GlobalExceptionHandler}.</p>
 */
public class NotFoundException extends Exception {

    /**
     * @param message human-readable explanation, returned as the response body
     */
    public NotFoundException(String message) {
        super(message);
    }
}
