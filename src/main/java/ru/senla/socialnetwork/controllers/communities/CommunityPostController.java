package ru.senla.socialnetwork.controllers.communities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Community Posts", description = "API для работы с постами сообществ")
public interface CommunityPostController {

  @Operation(summary = "Получить все посты сообщества")
  @ApiResponse(responseCode = "200", description = "Список постов сообщества",
      content = @Content(schema = @Schema(implementation = CommunityPostDTO[].class)))
  ResponseEntity<?> getAllPosts(Long communityId);

  @Operation(summary = "Получить закрепленные посты сообщества")
  @ApiResponse(responseCode = "200", description = "Список закрепленных постов",
      content = @Content(schema = @Schema(implementation = CommunityPostDTO[].class)))
  ResponseEntity<?> getPinnedPosts(Long communityId);

  @Operation(summary = "Получить пост по ID")
  @ApiResponse(responseCode = "200", description = "Информация о посте",
      content = @Content(schema = @Schema(implementation = CommunityPostDTO.class)))
  ResponseEntity<?> getById(Long communityId, Long postId);

  @Operation(summary = "Создать пост в сообществе")
  @ApiResponse(responseCode = "201", description = "Пост успешно создан",
      content = @Content(schema = @Schema(implementation = CommunityPostDTO.class)))
  ResponseEntity<?> create(Long communityId, CreateCommunityPostDTO dto, Authentication auth);

  @Operation(summary = "Удалить пост")
  @ApiResponse(responseCode = "200", description = "Пост успешно удален",
      content = @Content(schema = @Schema(implementation = DeleteResponseDTO.class)))
  ResponseEntity<?> delete(Long communityId, Long postId, Authentication auth);

  @Operation(summary = "Обновить пост")
  @ApiResponse(responseCode = "200", description = "Пост успешно обновлен",
      content = @Content(schema = @Schema(implementation = CommunityPostDTO.class)))
  ResponseEntity<?> update(Long communityId, Long postId, UpdateCommunityPostDTO dto,
                           Authentication auth);
}
