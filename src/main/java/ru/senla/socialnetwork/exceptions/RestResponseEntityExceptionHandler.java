package ru.senla.socialnetwork.exceptions;

import java.io.EOFException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.senla.socialnetwork.exceptions.friendRequests.FriendRequestException;
import ru.senla.socialnetwork.exceptions.general.EntitiesNotFoundException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
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

  @ExceptionHandler(EntitiesNotFoundException.class)
  protected ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntitiesNotFoundException ex,
                                                                        WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Ошибка при поиске");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
  }

//  @ExceptionHandler(EOFException.class)
//  public ResponseEntity<String> handleEOFException(EOFException e) {
//    return ResponseEntity
//        .status(HttpStatus.BAD_REQUEST)
//        .body("Invalid request body: " + e.getMessage());
//  }

  @ExceptionHandler(FriendRequestException.class)
  protected ResponseEntity<ProblemDetail> handleFriendRequestException(
      RuntimeException ex, WebRequest request) {
    log.warn("Ошибка при действии с friendRequest: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, ex.getMessage());
    problemDetail.setTitle("Ошибка при действии с friendRequest");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.UNAUTHORIZED);
  }
}