package ru.senla.socialnetwork.facade.chats;


import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberFacade {
  ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd);

  void removeUserFromChat(Long chatId, String userEmailToRemove, String currentUserEmail);

  ChatMemberDTO mute(Long chatId, String userEmailToMute,
                     ZonedDateTime muteUntil, String currentUserEmail);

  ChatMemberDTO unmute(Long chatId, String userEmailToMute, String currentUserEmail);

  void leave(Long chatId, String userEmail);

  ChatMemberDTO changeRole(Long chatId, String email,
                           MemberRole role, String currentUserEmail);

  boolean isChatMember(Long chatId, String email);
}
