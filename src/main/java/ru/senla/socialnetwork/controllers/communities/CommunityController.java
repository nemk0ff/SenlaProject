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
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Communities", description = "API для работы с сообществами")
public interface CommunityController {

  @Operation(summary = "Создать сообщество")
  @ApiResponse(responseCode = "201", description = "Сообщество создано",
      content = @Content(schema = @Schema(implementation = CommunityDTO.class)))
  ResponseEntity<?> create(CreateCommunityDTO dto, Authentication auth);

  @Operation(summary = "Удалить сообщество")
  @ApiResponse(responseCode = "200", description = "Сообщество удалено",
      content = @Content(schema = @Schema(implementation = DeleteResponseDTO.class)))
  ResponseEntity<?> delete(Long id, Authentication auth);

  @Operation(summary = "Получить информацию о сообществе")
  @ApiResponse(responseCode = "200", description = "Информация о сообществе",
      content = @Content(schema = @Schema(implementation = CommunityDTO.class)))
  ResponseEntity<?> get(Long id);

  @Operation(summary = "Изменить информацию о сообществе")
  @ApiResponse(responseCode = "200", description = "Информация о сообществе обновлена",
      content = @Content(schema = @Schema(implementation = CommunityDTO.class)))
  ResponseEntity<?> change(ChangeCommunityDTO changeCommunityDTO, Authentication auth);
}
