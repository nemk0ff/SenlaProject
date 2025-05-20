package ru.senla.socialnetwork.controllers.comments;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Comments", description = "API для работы с комментариями")
public interface CommentController {

  @Operation(summary = "Получить все комментарии (только для ADMIN)")
  @ApiResponse(responseCode = "200", description = "Список всех комментариев",
      content = @Content(schema = @Schema(implementation = CommentDTO[].class)))
  ResponseEntity<?> getAll();

  @Operation(summary = "Получить комментарий по ID")
  @ApiResponse(responseCode = "200", description = "Комментарий найден",
      content = @Content(schema = @Schema(implementation = CommentDTO.class)))
  ResponseEntity<?> get(Long id, Authentication auth);

  @Operation(summary = "Получить комментарии к посту")
  @ApiResponse(responseCode = "200", description = "Список комментариев поста",
      content = @Content(schema = @Schema(implementation = CommentDTO[].class)))
  ResponseEntity<?> getPostComments(Long postId, Authentication auth);

  @Operation(summary = "Создать комментарий")
  @ApiResponse(responseCode = "201", description = "Комментарий создан",
      content = @Content(schema = @Schema(implementation = CommentDTO.class)))
  ResponseEntity<?> createComment(Long postId, @Valid CreateCommentDTO request,
                                  Authentication auth);

  @Operation(summary = "Обновить комментарий")
  @ApiResponse(responseCode = "200", description = "Комментарий обновлен",
      content = @Content(schema = @Schema(implementation = CommentDTO.class)))
  ResponseEntity<?> updateComment(Long id, UpdateCommentDTO request, Authentication auth);

  @Operation(summary = "Удалить комментарий")
  @ApiResponse(responseCode = "200", description = "Комментарий удален",
      content = @Content(schema = @Schema(implementation = DeleteResponseDTO.class)))
  ResponseEntity<?> deleteComment(Long id, Authentication auth);
}