package ru.senla.socialnetwork.controllers.comments;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.model.comment.ReactionType;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reactions", description = "API для работы с реакциями на комментарии")
public interface ReactionController {

  @Operation(summary = "Получить все реакции (только для ADMIN)")
  @ApiResponse(responseCode = "200", description = "Список всех реакций",
      content = @Content(schema = @Schema(implementation = ReactionDTO[].class)))
  ResponseEntity<?> getAll();

  @Operation(summary = "Получить реакцию по ID")
  @ApiResponse(responseCode = "200", description = "Реакция найдена",
      content = @Content(schema = @Schema(implementation = ReactionDTO.class)))
  ResponseEntity<?> get(Long id, Authentication auth);

  @Operation(summary = "Получить реакции для комментария")
  @ApiResponse(responseCode = "200", description = "Список реакций комментария",
      content = @Content(schema = @Schema(implementation = ReactionDTO[].class)))
  ResponseEntity<?> getByComment(Long commentId, Authentication auth);

  @Operation(summary = "Добавить реакцию к комментарию")
  @ApiResponse(responseCode = "201", description = "Реакция создана",
      content = @Content(schema = @Schema(implementation = ReactionDTO.class)))
  ResponseEntity<?> createReaction(Long commentId, ReactionType reaction, Authentication auth);

  @Operation(summary = "Удалить реакцию")
  @ApiResponse(responseCode = "200", description = "Реакция удалена",
      content = @Content(schema = @Schema(implementation = DeleteResponseDTO.class)))
  ResponseEntity<?> removeReaction(Long id, Authentication auth);
}
