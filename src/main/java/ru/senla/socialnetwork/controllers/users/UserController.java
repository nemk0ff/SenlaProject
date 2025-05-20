package ru.senla.socialnetwork.controllers.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.hibernate.mapping.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.model.users.Gender;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "API для управления пользователями")
public interface UserController {

  @Operation(summary = "Получить пользователя по ID",
      description = "Возвращает информацию о пользователе по его идентификатору")
  @ApiResponse(responseCode = "200", description = "Информация о пользователе",
      content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
  ResponseEntity<?> get(Long id);

  @Operation(summary = "Получить пользователя по email",
      description = "Возвращает информацию о пользователе по его email")
  @ApiResponse(responseCode = "200", description = "Информация о пользователе",
      content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
  ResponseEntity<?> get(String email);

  @Operation(summary = "Поиск пользователей",
      description = "Поиск пользователей по различным параметрам. Все параметры необязательные.")
  @ApiResponse(responseCode = "200", description = "Список найденных пользователей",
      content = @Content(schema = @Schema(implementation = UserResponseDTO[].class)))
  ResponseEntity<?> find(String name, String surname, Gender gender, LocalDate birthdate);

  @Operation(summary = "Редактировать профиль",
      description = "Обновление информации о текущем пользователе")
  @ApiResponse(responseCode = "200", description = "Обновленная информация о пользователе",
      content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
  ResponseEntity<?> edit(@Valid UserRequestDTO editDTO, Authentication auth);

  @Operation(summary = "Изменить email",
      description = "Изменение email текущего пользователя. Возвращает новый JWT токен.")
  @ApiResponse(responseCode = "200", description = "Новый токен и обновленная информация о пользователе",
      content = @Content(schema = @Schema(implementation = Map.class,
          example = "{\"token\": \"new_jwt_token\", \"updatedEmail\": {...}}")))
  ResponseEntity<?> changeEmail(@NotBlank String newEmail, Authentication auth);
}
