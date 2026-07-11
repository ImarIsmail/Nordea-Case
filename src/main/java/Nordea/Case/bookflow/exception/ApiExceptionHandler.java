package Nordea.Case.bookflow.exception;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Converts domain and validation errors into consistent API responses.
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException exception) {
		return build(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException exception) {
		return build(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(WebExchangeBindException exception) {
		// Flatten field errors so the client gets a simple message list.
		List<String> messages = exception.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.toList();
		return build(HttpStatus.BAD_REQUEST, String.join(", ", messages));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneric(Exception exception) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + exception.getMessage());
	}

	private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(Map.of(
				"timestamp", Instant.now().toString(),
				"status", status.value(),
				"error", status.getReasonPhrase(),
				"message", message));
	}
}