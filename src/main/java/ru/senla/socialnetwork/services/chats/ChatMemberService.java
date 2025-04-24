package ru.senla.socialnetwork.services.chats;

import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberService {
  boolean isChatCreator(Long chatId, String email);

  ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd);

  void removeUserFromChat(Long chatId, String userEmailToRemove);

  ChatMemberDTO muteUser(Long chatId, String userEmailToMute, ZonedDateTime muteUntil);

  void leaveChat(Long chatId, String userEmail);

  boolean isUserInChat(Long chatId, String userEmail);

  ChatMemberDTO changeMemberRole(Long chatId, String userEmail, MemberRole newRole);
}
