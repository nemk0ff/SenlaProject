package ru.senla.socialnetwork.facades.chats.impl;

import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMemberMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.facades.chats.ChatMemberFacade;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.ChatService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatMemberFacadeImpl implements ChatMemberFacade {
  private final ChatMemberMapper chatMemberMapper;
  private final ChatService chatService;
  private final ChatMemberService chatMemberService;
  private final UserService userService;

  @Override
  public ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd) {
    Chat chat = chatService.get(chatId);

    if (chatMemberService.isChatMember(chat.getId(), userEmailToAdd)) {
      throw new ChatMemberException("Пользователь уже в чате");
    }

    User userToAdd = userService.getUserByEmail(userEmailToAdd);
    ChatMember newMember = ChatMember.builder()
        .chat(chat)
        .user(userToAdd)
        .role(MemberRole.MEMBER)
        .joinDate(ZonedDateTime.now())
        .build();

    return chatMemberMapper.memberToDTO(chatMemberService.addUserToChat(chat, newMember));
  }

  @Override
  public void removeUserFromChat(Long chatId, String userEmailToRemove, String currentUserEmail) {
    Chat chat = chatService.get(chatId);
    if (!chat.getIsGroup()) {
      throw new ChatMemberException("Нельзя удалить участника из личного чата. Удалите весь чат.");
    } else if (currentUserEmail.equals(userEmailToRemove)) {
      throw new ChatMemberException("Нельзя удалить самого себя. Используйте выход из чата");
    }
    ChatMember currentMember = chatMemberService.getMember(chatId, currentUserEmail);
    ChatMember removingMember = chatMemberService.getMember(chatId, userEmailToRemove);

    if (currentMember.getRole().equals(MemberRole.MEMBER)) {
      throw new ChatMemberException("У вас недостаточно прав для удаления пользователя из чата");
    } else if (currentMember.getRole().equals(MemberRole.MODERATOR)
        && !removingMember.getRole().equals(MemberRole.MEMBER)) {
      throw new ChatMemberException("Вы можете удалить из чата только обычного участника");
    }
    chatMemberService.removeMember(removingMember);
  }

  @Override
  public ChatMemberDTO mute(Long chatId, String userEmailToMute, ZonedDateTime muteUntil) {
    return chatMemberMapper.memberToDTO(chatMemberService.mute(chatId, userEmailToMute, muteUntil));
  }

  @Override
  public ChatMemberDTO unmute(Long chatId, String userEmailToMute) {
    return chatMemberMapper.memberToDTO(chatMemberService.unmute(chatId, userEmailToMute));
  }

  @Override
  public void leave(Long chatId, String userEmail) {
    chatMemberService.leave(chatId, userEmail);
  }

  @Override
  public ChatMemberDTO changeRole(Long chatId, String email, MemberRole role) {
    return chatMemberMapper.memberToDTO(chatMemberService.changeRole(chatId, email, role));
  }

  @Override
  public boolean isChatMember(Long chatId, String email) {
    return chatMemberService.isChatMember(chatId, email);
  }

  @Override
  public boolean isChatAdmin(Long chatId, String requesterEmail) {
    ChatMember currentMember = chatMemberService.getMember(chatId, requesterEmail);
    return currentMember.getRole().equals(MemberRole.ADMIN);
  }

  @Override
  public boolean isChatAdminOrModerator(Long chatId, String requesterEmail) {
    ChatMember currentMember = chatMemberService.getMember(chatId, requesterEmail);
    return currentMember.getRole().equals(MemberRole.MODERATOR)
        || currentMember.getRole().equals(MemberRole.ADMIN);
  }
}
