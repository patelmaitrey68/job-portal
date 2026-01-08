package com.jobportal.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jobportal.exception.JobPortalException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionControllerAdvice {

	@Autowired
	private Environment environment;

	@ExceptionHandler(JobPortalException.class)
	public ResponseEntity<ErrorInfo> jobPortalExceptionHandler(JobPortalException exception) {
		String message = environment.getProperty(exception.getMessage());
		if (message == null) {
			message = exception.getMessage();
		}
		ErrorInfo error = new ErrorInfo(message, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
	public ResponseEntity<ErrorInfo> validatorExceptionHandler(Exception exception) {
		String msg = "";
		if (exception instanceof MethodArgumentNotValidException manvException) {
			msg = manvException.getAllErrors().stream().map(ObjectError::getDefaultMessage)
					.collect(Collectors.joining(", "));
		} else {
			ConstraintViolationException cvException = (ConstraintViolationException) exception;
			msg = cvException.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
					.collect(Collectors.joining(", "));
		}
		ErrorInfo error = new ErrorInfo(msg, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> generalException(Exception exception) {
		ErrorInfo error = new ErrorInfo(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

