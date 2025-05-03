package ru.senla.socialnetwork.facades.chats;


import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberFacade {
  ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd);

  void removeUserFromChat(Long chatId, String userEmailToRemove, String currentUserEmail);

  ChatMemberDTO mute(Long chatId, String userEmailToMute, ZonedDateTime muteUntil);

  ChatMemberDTO unmute(Long chatId, String userEmailToMute);

  void leave(Long chatId, String userEmail);

  ChatMemberDTO changeRole(Long chatId, String email,
                           MemberRole role);

  boolean isChatMember(Long chatId, String email);

  boolean isChatAdmin(Long chatId, String requesterEmail);

  boolean isChatModerator(Long chatId, String requesterEmail);

  boolean isChatAdminOrModerator(Long chatId, String requesterEmail);
}
