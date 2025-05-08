package ru.senla.socialnetwork.facades.chats;


import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberFacade {
  ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd, String client);

  void removeUserFromChat(Long chatId, String userEmailToRemove, String currentUserEmail);

  ChatMemberDTO mute(Long chatId, String userEmailToMute, ZonedDateTime muteUntil, String clientEmail);

  ChatMemberDTO unmute(Long chatId, String userEmailToMute, String clientEmail);

  void leave(Long chatId, String userEmail);

  ChatMemberDTO changeRole(Long chatId, String email, MemberRole role, String clientName);

  boolean isChatMember(Long chatId, String email);

  boolean isChatAdminOrModerator(Long chatId, String requesterEmail);
}
