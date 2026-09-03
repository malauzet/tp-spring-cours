package fr.diginamic.demospring.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Uniform error payload returned for <em>every</em> failed request, whatever the
 * cause (business rule, bean validation, malformed JSON, wrong HTTP method…).
 *
 * <p>Produced exclusively by {@link GlobalExceptionHandler}. Successful
 * responses never use this shape.</p>
 *
 * @param timestamp moment the error was produced
 * @param status    HTTP status code (e.g. {@code 400})
 * @param error     HTTP reason phrase (e.g. {@code "Bad Request"})
 * @param message   human-readable explanation, safe to show to the caller
 * @param path      request path that produced the error
 */
@Schema(description = "Uniform error payload returned for every failed request.")
public record ApiError(
        @Schema(description = "Moment the error was produced.", example = "2026-09-03T10:15:30Z")
        OffsetDateTime timestamp,

        @Schema(description = "HTTP status code.", example = "400")
        int status,

        @Schema(description = "HTTP reason phrase.", example = "Bad Request")
        String error,

        @Schema(description = "Human-readable explanation.", example = "name: City name cannot be null.")
        String message,

        @Schema(description = "Request path that produced the error.", example = "/cities")
        String path) {
}
