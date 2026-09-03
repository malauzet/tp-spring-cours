package fr.diginamic.demospring.exception;

/**
 * Checked exception for invalid business operations (bad input, conflicting
 * state, empty search result).
 *
 * <p>Mapped to HTTP {@code 400 Bad Request} by
 * {@link GlobalExceptionHandler}.</p>
 */
public class FunctionalException extends Exception {

    /**
     * @param message human-readable explanation, returned as the response body
     */
    public FunctionalException(String message) {
        super(message);
    }
}
