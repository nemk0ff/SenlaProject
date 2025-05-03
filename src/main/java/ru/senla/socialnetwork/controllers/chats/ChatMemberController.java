package ru.senla.socialnetwork.controllers.chats;

import java.time.ZonedDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberController {
  ResponseEntity<ChatMemberDTO> addMember(Long chatId, String userEmail);

  ResponseEntity<Void> removeMember(Long chatId, String userEmail, Authentication auth);

  ResponseEntity<ChatMemberDTO> muteMember(
      Long chatId, String userEmail, ZonedDateTime muteUntil, Authentication auth);

  ResponseEntity<ChatMemberDTO> unmuteMember(Long chatId, String userEmail, Authentication auth);

  ResponseEntity<Void> leaveChat(Long chatId, String userEmail);

  ResponseEntity<ChatMemberDTO> changeMemberRole(
      Long chatId, String email, MemberRole role, Authentication auth);
}
