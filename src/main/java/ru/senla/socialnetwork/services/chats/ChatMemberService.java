package ru.senla.socialnetwork.services.chats;

import java.time.ZonedDateTime;
import java.util.List;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;

public interface ChatMemberService {
  ChatMember addUserToChat(Chat chat, User user);

  ChatMember mute(Long chatId, String userEmailToMute, ZonedDateTime muteUntil);

  ChatMember unmute(Long chatId, String userEmail);

  ChatMember leave(Long chatId, String userEmail);

  ChatMember changeRole(Long chatId, ChatMember member, MemberRole newRole);

  ChatMember getMember(Long chatId, String email);

  List<ChatMember> getMembers(Long chatId);

  ChatMember removeMember(ChatMember member);

  void deleteMember(ChatMember member);

  boolean isChatMember(Long chatId, String email);

  boolean isChatMemberExists(Long chatId, String email);

  ChatMember recreate(ChatMember member);

  void saveMembers(List<ChatMember> members);
}
