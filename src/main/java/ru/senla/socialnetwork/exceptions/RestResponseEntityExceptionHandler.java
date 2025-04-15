package ru.senla.socialnetwork.exceptions;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import java.io.EOFException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Ошибка при поиске");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(EOFException.class)
  public ResponseEntity<String> handleEOFException(EOFException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("Invalid request body: " + e.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<ProblemDetail> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {

    log.warn("Доступ запрещен: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.FORBIDDEN, "Недостаточно прав для выполнения операции: " + ex.getMessage());
    problemDetail.setTitle("Доступ запрещен");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({
      JwtException.class,
      AuthenticationException.class,
      BadCredentialsException.class,
      InsufficientAuthenticationException.class
  })
  protected ResponseEntity<ProblemDetail> handleAuthenticationException(
      RuntimeException ex, WebRequest request) {

    log.warn("Ошибка аутентификации: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, "Требуется авторизация: " + ex.getMessage());
    problemDetail.setTitle("Ошибка аутентификации");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.UNAUTHORIZED);
  }
}