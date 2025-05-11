package ru.senla.socialnetwork.exceptions;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
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
import ru.senla.socialnetwork.exceptions.auth.UserNotRegisteredException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
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

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    log.warn("Ошибка валидации параметров: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder(
        "Ошибка валидации параметров",
        request,
        HttpStatus.BAD_REQUEST,
        ex);

    List<String> errors = ex.getConstraintViolations().stream()
        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
        .toList();
    problemDetail.setProperty("errors", errors);

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

  @ExceptionHandler({
      AuthenticationException.class,
      UserNotRegisteredException.class
  })
  protected ResponseEntity<ProblemDetail> handleAuthenticationException(
      Exception ex, WebRequest request) {
    log.warn("Ошибка аутентификации: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder("Ошибка аутентификации",
        request, HttpStatus.UNAUTHORIZED, ex);

    return new ResponseEntity<>(problemDetail, HttpStatus.UNAUTHORIZED);
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

  @ExceptionHandler(DataRetrievalFailureException.class)
  protected ResponseEntity<ProblemDetail> handleDataRetrievalFailureException(
      DataRetrievalFailureException ex, WebRequest request) {
    log.error("Ошибка доступа к данным: {}", ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder(
        "Ошибка доступа к данным",
        request,
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex
    );
    problemDetail.setDetail("Произошла ошибка при работе с базой данных");

    return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({SocialNetworkException.class})
  public ResponseEntity<ProblemDetail> handleBusinessExceptions(
      Exception ex, WebRequest request) {
    String title = ex instanceof SocialNetworkException
        ? ((SocialNetworkException)ex).getAction()
        : "Возникла бизнес-ошибка во время работы приложения";

    log.warn("{}: {}", title, ex.getMessage());

    ProblemDetail problemDetail = problemDetailBuilder(title, request, HttpStatus.BAD_REQUEST, ex);
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