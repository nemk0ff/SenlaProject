package ru.senla.socialnetwork.controllers.feed;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.PostDTO;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Feed", description = "API для работы с лентой новостей")
public interface FeedController {

  @Operation(summary = "Получить ленту новостей")
  @ApiResponse(responseCode = "200", description = "Лента новостей пользователя",
      content = @Content(schema = @Schema(implementation = PostDTO[].class)))
  List<PostDTO> newsFeed(Authentication auth);
}