package ru.senla.socialnetwork.services.chats;

import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberService {

  ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd);

  void removeUserFromChat(Long chatId, String userEmailToRemove, String currentUserEmail);

  ChatMemberDTO mute(Long chatId, String userEmailToMute,
                     ZonedDateTime muteUntil, String currentUserEmail);

  void leave(Long chatId, String userEmail);

  ChatMemberDTO changeRole(Long chatId, String userEmail,
                           MemberRole newRole, String currentUserEmail);
}
