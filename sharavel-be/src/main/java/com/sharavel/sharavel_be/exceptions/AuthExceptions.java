package com.sharavel.sharavel_be.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.sharavel.sharavel_be.exceptions.dto.ErrorResponse;

@RestControllerAdvice
public class AuthExceptions {
	private static final Logger log = LoggerFactory.getLogger(AuthExceptions.class);

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
		log.warn("ResponseStatusException: Status={} Reason={}", ex.getStatusCode(), ex.getReason());
		// errorCode는 필요에 따라 ex.getReason()을 기반으로 생성하거나 고정값 사용
		ErrorResponse error = new ErrorResponse(ex.getStatusCode().value(), ex.getReason(), "API_ERROR");
		return new ResponseEntity<>(error, ex.getStatusCode());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
		log.warn("Authentication failed - Bad Credentials: {}", ex.getMessage());
		ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password.",
				"INVALID_CREDENTIALS");
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
		log.error("Illegal state encountered: {}", ex.getMessage(), ex);
		ErrorResponse error = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"An internal server error occurred due to an invalid state. Please try again later.",
				"ILLEGAL_STATE");
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
		log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
		ErrorResponse error = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"An unexpected error occurred. Please try again later.",
				"UNEXPECTED_ERROR");
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
