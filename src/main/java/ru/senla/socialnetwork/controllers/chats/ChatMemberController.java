package ru.senla.socialnetwork.controllers.chats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import java.time.ZonedDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.MemberRole;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat Members", description = "API для управления участниками чатов")
public interface ChatMemberController {

  @Operation(summary = "Добавить участника в чат")
  @ApiResponse(responseCode = "200", description = "Участник успешно добавлен",
      content = @Content(schema = @Schema(implementation = ChatMemberDTO.class)))
  ResponseEntity<?> addMember(Long chatId, @Email String email, Authentication auth);

  @Operation(summary = "Удалить участника из чата")
  @ApiResponse(responseCode = "200", description = "Участник успешно удален",
      content = @Content(schema = @Schema(implementation = ChatMemberDTO.class)))
  ResponseEntity<?> removeMember(Long chatId, @Email String email, Authentication auth);

  @Operation(summary = "Замутить участника")
  @ApiResponse(responseCode = "200", description = "Участник успешно замьючен",
      content = @Content(schema = @Schema(implementation = ChatMemberDTO.class)))
  ResponseEntity<?> muteMember(Long chatId, @Email String email,
                               ZonedDateTime muteUntil, Authentication auth);

  @Operation(summary = "Размутить участника")
  @ApiResponse(responseCode = "200", description = "Участник успешно размьючен",
      content = @Content(schema = @Schema(implementation = ChatMemberDTO.class)))
  ResponseEntity<?> unmuteMember(Long chatId, @Email String email, Authentication auth);

  @Operation(summary = "Покинуть чат")
  @ApiResponse(responseCode = "200", description = "Успешный выход из чата",
      content = @Content(schema = @Schema(implementation = ChatMemberDTO.class)))
  ResponseEntity<?> leaveChat(Long chatId, Authentication auth);

  @Operation(summary = "Изменить роль участника")
  @ApiResponse(responseCode = "200", description = "Роль успешно изменена",
      content = @Content(schema = @Schema(implementation = ChatMemberDTO.class)))
  ResponseEntity<?> changeMemberRole(
      Long chatId, @Email String email,
      MemberRole role, Authentication auth);
}
