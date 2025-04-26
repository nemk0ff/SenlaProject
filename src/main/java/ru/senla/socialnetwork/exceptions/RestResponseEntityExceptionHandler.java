package ru.senla.socialnetwork.exceptions;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
import ru.senla.socialnetwork.exceptions.friendRequests.FriendRequestException;
import ru.senla.socialnetwork.exceptions.users.UserException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, WebRequest request) {
    log.warn("Некорректный JSON: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder(
        "Некорректный запрос", request, HttpStatus.BAD_REQUEST, ex);
    problemDetail.setDetail("Проверьте формат и типы данных в теле запроса");

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    log.warn("Ошибка парсинга параметра '{}': ожидался тип {}",
        ex.getName(), ex.getRequiredType());

    ProblemDetail problemDetail = problemDetailBuilder(
        "Неверный тип параметра", request, HttpStatus.BAD_REQUEST, ex);

    problemDetail.setDetail(String.format(
        "Параметр '%s' должен быть типа %s", ex.getName(),
        Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
       @Nullable HttpHeaders headers,
       @Nullable HttpStatusCode status,
       @Nullable WebRequest request) {
    log.warn("Ошибка валидации данных: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder("Ошибка валидации данных",
        request, HttpStatus.BAD_REQUEST, ex);

    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();
    problemDetail.setProperty("errors", errors);

    return new ResponseEntity<>(problemDetail, headers, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthenticationException.class)
  protected ResponseEntity<ProblemDetail> handleAuthenticationException(
      AuthenticationException ex, WebRequest request) {
    log.warn("Ошибка аутентификации: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder("Ошибка аутентификации",
        request, HttpStatus.UNAUTHORIZED, ex);

    return new ResponseEntity<>(problemDetail, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UserException.class)
  protected ResponseEntity<ProblemDetail> handleUserNotRegisteredException(
      UserException ex, WebRequest request) {
    log.warn("{}: {}", ex.getAction(), ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder(ex.getAction(),
        request, HttpStatus.BAD_REQUEST, ex);

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<ProblemDetail> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    log.warn("Доступ запрещен: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder("Доступ запрещён",
        request, HttpStatus.FORBIDDEN, ex);

    return new ResponseEntity<>(problemDetail, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<ProblemDetail> handleEntityNotFoundException(
      EntityNotFoundException ex, WebRequest request) {
    log.warn("Ошибка при поиске: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder("Ошибка при поиске",
        request, HttpStatus.NOT_FOUND, ex);

    return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(FriendRequestException.class)
  protected ResponseEntity<ProblemDetail> handleFriendRequestException(
      FriendRequestException ex, WebRequest request) {
    log.warn("Ошибка при действии с заявкой в друзья: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder("Ошибка при действии с заявкой в друзья",
        request, HttpStatus.BAD_REQUEST, ex);

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(ChatException.class)
  protected ResponseEntity<ProblemDetail> handleFriendRequestException(
      ChatException ex, WebRequest request) {
    log.warn("{}: {}", ex.getAction(), ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder(
        ex.getAction(), request, HttpStatus.BAD_REQUEST, ex);

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  private ProblemDetail problemDetailBuilder(String title, WebRequest request,
                                             HttpStatus status, Exception ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        status, ex.getMessage());
    problemDetail.setTitle(title);
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());
    return problemDetail;
  }
}