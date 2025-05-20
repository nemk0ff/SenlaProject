package ru.senla.socialnetwork.controllers.communities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.communitites.BanCommunityMemberDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.model.MemberRole;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Community Members", description = "API для управления участниками сообществ")
public interface CommunityMemberController {

  @Operation(summary = "Получить всех участников сообщества")
  @ApiResponse(responseCode = "200", description = "Список участников сообщества",
      content = @Content(schema = @Schema(implementation = CommunityMemberDTO[].class)))
  ResponseEntity<?> getAll(Long communityId, Authentication auth);

  @Operation(summary = "Вступить в сообщество")
  @ApiResponse(responseCode = "200", description = "Пользователь успешно вступил в сообщество",
      content = @Content(schema = @Schema(implementation = CommunityMemberDTO.class)))
  ResponseEntity<?> joinCommunity(Long communityId, Authentication auth);

  @Operation(summary = "Покинуть сообщество")
  @ApiResponse(responseCode = "200", description = "Пользователь успешно покинул сообщество",
      content = @Content(schema = @Schema(implementation = CommunityMemberDTO.class)))
  ResponseEntity<?> leaveCommunity(Long communityId, Authentication auth);

  @Operation(summary = "Заблокировать участника")
  @ApiResponse(responseCode = "200", description = "Участник успешно заблокирован",
      content = @Content(schema = @Schema(implementation = CommunityMemberDTO.class)))
  ResponseEntity<?> banMember(Long communityId, @Valid BanCommunityMemberDTO dto, Authentication auth);

  @Operation(summary = "Разблокировать участника")
  @ApiResponse(responseCode = "200", description = "Участник успешно разблокирован",
      content = @Content(schema = @Schema(implementation = CommunityMemberDTO.class)))
  ResponseEntity<?> unbanMember(Long communityId, String email, Authentication auth);

  @Operation(summary = "Изменить роль участника")
  @ApiResponse(responseCode = "200", description = "Роль участника успешно изменена",
      content = @Content(schema = @Schema(implementation = CommunityMemberDTO.class)))
  ResponseEntity<?> changeMemberRole(Long communityId, String userEmail, MemberRole role, Authentication auth);
}