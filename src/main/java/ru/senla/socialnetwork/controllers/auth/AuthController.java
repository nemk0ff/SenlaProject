package ru.senla.socialnetwork.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.AuthResponseDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;

@Tag(name = "Authentication",
    description = "API для аутентификации и регистрации пользователей")
@SecurityRequirement(name = "bearerAuth")
public interface AuthController {

  @Operation(summary = "Регистрация нового пользователя",
      requestBody = @RequestBody(description = "Данные для регистрации", required = true,
          content = @Content(schema = @Schema(implementation = RegisterDTO.class))))
  @ApiResponse(responseCode = "200", description = "Успешная регистрация")
  ResponseEntity<?> register(@Valid RegisterDTO registerDTO);


  @Operation(summary = "Аутентификация пользователя",
      description = "Авторизует пользователя и возвращает JWT токен",
      requestBody = @RequestBody(description = "Данные для входа (email и пароль пользователя)",
          required = true,
          content = @Content(schema = @Schema(implementation = AuthRequestDTO.class))))
  @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
      content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
  ResponseEntity<?> login(@Valid AuthRequestDTO request);
}