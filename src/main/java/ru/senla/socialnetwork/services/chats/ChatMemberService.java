package ru.senla.socialnetwork.services.chats;

import java.time.ZonedDateTime;
import java.util.List;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberService {
  ChatMember addUserToChat(Chat chat, ChatMember newMember);

  ChatMember mute(Long chatId, String userEmailToMute, ZonedDateTime muteUntil);

  ChatMember unmute(Long chatId, String userEmail);

  void leave(Long chatId, String userEmail);

  ChatMember changeRole(Long chatId, ChatMember member, MemberRole newRole);

  ChatMember getMember(Long chatId, String email);

  List<ChatMember> getMembers(Long chatId);

  void removeMember(ChatMember member);

  boolean isChatMember(Long chatId, String email);

  void saveMembers(List<ChatMember> members);
}
