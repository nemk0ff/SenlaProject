package ru.senla.socialnetwork.controllers.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Wall Posts", description = "API для управления постами на стене пользователя")
public interface WallPostController {

  @Operation(summary = "Получить все посты пользователя",
      description = "Возвращает список всех постов на стене указанного пользователя")
  @ApiResponse(responseCode = "200", description = "Список постов пользователя",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = WallPostResponseDTO.class))))
  ResponseEntity<List<?>> getAll(String email, Authentication auth);

  @Operation(summary = "Получить пост по ID",
      description = "Возвращает конкретный пост по его идентификатору")
  @ApiResponse(responseCode = "200", description = "Найденный пост",
      content = @Content(schema = @Schema(implementation = WallPostResponseDTO.class)))
  ResponseEntity<?> getById(Long postId, Authentication auth);

  @Operation(summary = "Создать новый пост",
      description = "Создает новый пост на стене текущего или другого пользователя")
  @ApiResponse(responseCode = "201", description = "Пост успешно создан",
      content = @Content(schema = @Schema(implementation = WallPostResponseDTO.class)))
  ResponseEntity<?> create(WallPostRequestDTO dto, Authentication auth);

  @Operation(summary = "Удалить пост",
      description = "Удаляет существующий пост по его идентификатору")
  @ApiResponse(responseCode = "200", description = "Пост успешно удален",
      content = @Content(schema = @Schema(implementation = DeleteResponseDTO.class)))
  ResponseEntity<?> delete(Long postId, Authentication auth);

  @Operation(summary = "Обновить пост",
      description = "Обновляет содержимое существующего поста")
  @ApiResponse(responseCode = "200", description = "Пост успешно обновлен",
      content = @Content(schema = @Schema(implementation = WallPostResponseDTO.class)))
  ResponseEntity<?> update(Long postId, WallPostRequestDTO dto, Authentication auth);
}
