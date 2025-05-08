package ru.senla.socialnetwork.controllers.chats;

import java.time.ZonedDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberController {
  ResponseEntity<ChatMemberDTO> addMember(Long chatId, String email, Authentication auth);

  ResponseEntity<String> removeMember(Long chatId, String email, Authentication auth);

  ResponseEntity<ChatMemberDTO> muteMember(Long chatId, String email,
                                           ZonedDateTime muteUntil, Authentication auth);

  ResponseEntity<ChatMemberDTO> unmuteMember(Long chatId, String email, Authentication auth);

  ResponseEntity<String> leaveChat(Long chatId, Authentication auth);

  ResponseEntity<ChatMemberDTO> changeMemberRole(Long chatId, String email, MemberRole role,
                                                 Authentication auth);
}
