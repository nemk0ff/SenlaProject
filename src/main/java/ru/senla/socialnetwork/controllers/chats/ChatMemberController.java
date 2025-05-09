package ru.senla.socialnetwork.controllers.chats;

import java.time.ZonedDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberController {
  ResponseEntity<?> addMember(Long chatId, String email, Authentication auth);

  ResponseEntity<?> removeMember(Long chatId, String email, Authentication auth);

  ResponseEntity<?> muteMember(Long chatId, String email,
                                           ZonedDateTime muteUntil, Authentication auth);

  ResponseEntity<?> unmuteMember(Long chatId, String email, Authentication auth);

  ResponseEntity<?> leaveChat(Long chatId, Authentication auth);

  ResponseEntity<?> changeMemberRole(Long chatId, String email, MemberRole role,
                                                 Authentication auth);
}
