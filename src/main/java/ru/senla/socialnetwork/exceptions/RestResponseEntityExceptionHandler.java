package ru.senla.socialnetwork.exceptions;

import java.io.EOFException;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.senla.socialnetwork.exceptions.friendRequests.FriendRequestException;
import ru.senla.socialnetwork.exceptions.general.EntitiesNotFoundException;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.exceptions.users.UserNotRegisteredException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {

    log.warn("Ошибка валидации данных: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "Validation failed");
    problemDetail.setTitle("Ошибка валидации данных");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();
    problemDetail.setProperty("errors", errors);

    return new ResponseEntity<>(problemDetail, headers, status);
  }

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

  @ExceptionHandler(EmailAlreadyExistsException.class)
  protected ResponseEntity<ProblemDetail> handleTakeEmailException(
      RuntimeException ex, WebRequest request) {

    log.warn("Ошибка при попытке использования email: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.CONFLICT, String.format("Email '%s' уже используется", ex.getMessage()));
    problemDetail.setTitle("Email conflict");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UserNotRegisteredException.class)
  protected ResponseEntity<ProblemDetail> handleUserNotRegisteredException(
      UserNotRegisteredException ex, WebRequest request) {

    log.warn("Попытка указания незарегистрированного email: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("User Not Found");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());

    return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
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

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }
}