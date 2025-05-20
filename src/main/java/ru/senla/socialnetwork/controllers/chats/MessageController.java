package ru.senla.socialnetwork.controllers.chats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.dto.chats.MessageResponseDTO;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Messages", description = "API для работы с сообщениями в чатах")
public interface MessageController {

  @Operation(summary = "Отправить сообщение в чат")
  @ApiResponse(responseCode = "201", description = "Сообщение успешно отправлено",
      content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
  ResponseEntity<?> sendMessage(Long chatId, @Valid MessageRequestDTO request, Authentication auth);

  @Operation(summary = "Получить все сообщения чата")
  @ApiResponse(responseCode = "200", description = "Список сообщений чата",
      content = @Content(schema = @Schema(implementation = MessageResponseDTO[].class)))
  ResponseEntity<?> getMessages(Long chatId, Authentication auth);

  @Operation(summary = "Получить конкретное сообщение")
  @ApiResponse(responseCode = "200", description = "Информация о сообщении",
      content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
  ResponseEntity<?> getMessage(Long chatId, Long messageId, Authentication auth);

  @Operation(summary = "Получить ответы на сообщение")
  @ApiResponse(responseCode = "200", description = "Список ответов на сообщение",
      content = @Content(schema = @Schema(implementation = MessageResponseDTO[].class)))
  ResponseEntity<?> getAnswers(Long chatId, Long messageId, Authentication auth);

  @Operation(summary = "Получить закрепленные сообщения")
  @ApiResponse(responseCode = "200", description = "Список закрепленных сообщений",
      content = @Content(schema = @Schema(implementation = MessageResponseDTO[].class)))
  ResponseEntity<?> getPinnedMessages(Long chatId, Authentication auth);

  @Operation(summary = "Закрепить сообщение")
  @ApiResponse(responseCode = "200", description = "Сообщение успешно закреплено",
      content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
  ResponseEntity<?> pinMessage(Long chatId, Long messageId, Authentication auth);

  @Operation(summary = "Открепить сообщение")
  @ApiResponse(responseCode = "200", description = "Сообщение успешно откреплено",
      content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
  ResponseEntity<?> unpinMessage(Long chatId, Long messageId, Authentication auth);

  @Operation(summary = "Удалить сообщение")
  @ApiResponse(responseCode = "200", description = "Сообщение успешно удалено",
      content = @Content(schema = @Schema(implementation = DeleteResponseDTO.class)))
  ResponseEntity<?> deleteMessage(Long chatId, Long messageId, Authentication auth);
}