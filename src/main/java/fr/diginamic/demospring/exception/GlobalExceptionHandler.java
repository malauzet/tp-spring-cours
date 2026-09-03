package fr.diginamic.demospring.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Translates every exception into an {@link ApiError} HTTP response, so clients
 * always get the same JSON shape regardless of where the failure originated.
 *
 * <table border="1">
 *   <caption>Exception &rarr; status mapping</caption>
 *   <tr><th>Exception</th><th>Status</th></tr>
 *   <tr><td>{@link NotFoundException}, {@link NoResourceFoundException}</td><td>404</td></tr>
 *   <tr><td>{@link CityException}</td><td>400</td></tr>
 *   <tr><td>{@link MethodArgumentNotValidException} (invalid {@code @RequestBody})</td><td>400</td></tr>
 *   <tr><td>{@link HandlerMethodValidationException}, {@link ConstraintViolationException}
 *           (invalid path/query parameter)</td><td>400</td></tr>
 *   <tr><td>{@link MethodArgumentTypeMismatchException} (e.g. letters where an int is expected)</td><td>400</td></tr>
 *   <tr><td>{@link HttpMessageNotReadableException} (malformed JSON)</td><td>400</td></tr>
 *   <tr><td>{@link IllegalArgumentException}</td><td>400</td></tr>
 *   <tr><td>{@link HttpRequestMethodNotSupportedException}</td><td>405</td></tr>
 *   <tr><td>{@link HttpMediaTypeNotSupportedException}</td><td>415</td></tr>
 *   <tr><td>anything else</td><td>500 (logged, generic message)</td></tr>
 * </table>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @param ex  the not-found exception
     * @param req current request, used to fill {@link ApiError#path()}
     * @return {@code 404} with the exception message
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /**
     * @param ex  the business exception
     * @param req current request
     * @return {@code 400} with the exception message
     */
    @ExceptionHandler(CityException.class)
    public ResponseEntity<ApiError> handleCityException(CityException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    /**
     * Bean Validation failure on a {@code @Valid @RequestBody}. Reports every
     * violated field, not just the first.
     *
     * @param ex  the validation exception raised by Spring MVC
     * @param req current request
     * @return {@code 400} listing each {@code field: message}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBodyValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message.isEmpty() ? "Validation error." : message, req);
    }

    /**
     * Bean Validation failure on a constrained {@code @PathVariable} /
     * {@code @RequestParam} (e.g. {@code @Positive}, {@code @Min}).
     *
     * @param ex  the method-validation exception
     * @param req current request
     * @return {@code 400} listing each violated constraint
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleParamValidation(HandlerMethodValidationException ex, HttpServletRequest req) {
        String message = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message.isEmpty() ? "Invalid request parameter." : message, req);
    }

    /**
     * @param ex  the constraint-violation exception (parameter validation, older style)
     * @param req current request
     * @return {@code 400} listing each violated constraint
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message.isEmpty() ? "Constraint violation." : message, req);
    }

    /**
     * A path or query parameter could not be converted to the declared type
     * (e.g. {@code /cities/abc} where an int is expected).
     *
     * @param ex  the type-mismatch exception
     * @param req current request
     * @return {@code 400} naming the parameter and expected type
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String type = ex.getRequiredType() == null ? "the expected type" : ex.getRequiredType().getSimpleName();
        return build(HttpStatus.BAD_REQUEST, "Parameter '" + ex.getName() + "' must be of type " + type + ".", req);
    }

    /**
     * @param ex  the parse exception (missing, malformed or untypeable JSON body)
     * @param req current request
     * @return {@code 400} with a generic message (parser details are not leaked)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body.", req);
    }

    /**
     * @param ex  the illegal-argument exception
     * @param req current request
     * @return {@code 400} with the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    /**
     * @param ex  the wrong-HTTP-method exception
     * @param req current request
     * @return {@code 405} with the exception message
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req);
    }

    /**
     * @param ex  the unsupported-content-type exception
     * @param req current request
     * @return {@code 415} with the exception message
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), req);
    }

    /**
     * No handler (and no static resource) matched the request.
     *
     * @param ex  the no-resource exception
     * @param req current request
     * @return {@code 404} naming the missing endpoint
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "No endpoint " + req.getMethod() + " " + req.getRequestURI() + ".", req);
    }

    /**
     * Safety net: anything not matched above is a bug, not a client error. The
     * cause is logged server-side; the client only gets a generic message.
     *
     * @param ex  the unexpected exception
     * @param req current request
     * @return {@code 500} with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", req);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req) {
        ApiError body = new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(),
                message, req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
