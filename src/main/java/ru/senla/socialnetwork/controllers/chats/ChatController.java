package ru.senla.socialnetwork.controllers.chats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat", description = "API для управление чатами")
public interface ChatController {

  @Operation(summary = "Получить все чаты пользователя")
  @ApiResponse(responseCode = "200", description = "OK",
      content = @Content(schema = @Schema(implementation = ChatDTO[].class)))
  ResponseEntity<?> getUserChats(Authentication auth);

  @Operation(summary = "Создать групповой чат")
  @ApiResponse(responseCode = "200", description = "OK",
      content = @Content(schema = @Schema(implementation = ChatDTO.class)))
  ResponseEntity<?> createGroupChat(@Valid CreateGroupChatDTO request, Authentication auth);

  @Operation(summary = "Создать личный чат",
      parameters = {@Parameter(name = "participant", description = "Email участника чата",
              required = true, in = ParameterIn.QUERY,
              schema = @Schema(type = "string", format = "email"))})
  @ApiResponse(responseCode = "200", description = "Личный чат создан",
      content = @Content(schema = @Schema(implementation = ChatDTO.class)))
  ResponseEntity<?> createPersonalChat(@Email String participantEmail, Authentication auth);

  @Operation(summary = "Удалить чат",
      parameters = {@Parameter(name = "chatId", description = "ID чата для удаления",
              required = true, in = ParameterIn.PATH,
              schema = @Schema(type = "integer", format = "int64"))})
  @ApiResponse(responseCode = "200", description = "Чат удален",
      content = @Content(schema = @Schema(implementation = DeleteResponseDTO.class)))
  ResponseEntity<?> deleteChat(Long chatId, Authentication auth);

  @Operation(summary = "Получить информацию о чате",
      parameters = {@Parameter(name = "chatId", description = "ID чата", required = true,
              in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64"))})
  @ApiResponse(responseCode = "200", description = "Информация о чате",
      content = @Content(schema = @Schema(implementation = ChatDTO.class)))
  ResponseEntity<?> getChat(Long chatId, Authentication auth);
}
