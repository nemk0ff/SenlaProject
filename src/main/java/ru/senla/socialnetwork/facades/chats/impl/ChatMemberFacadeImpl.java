package ru.senla.socialnetwork.facades.chats.impl;

import java.time.ZonedDateTime;
import java.util.Optional;
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
import ru.senla.socialnetwork.model.MemberRole;
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
  public ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd, String clientEmail) {
    if (!chatMemberService.isChatMember(chatId, clientEmail)) {
      throw new ChatMemberException("Вы не можете добавить участника, т.к. не являетесь участником этого чата");
    }

    Chat chat = chatService.get(chatId);

    Optional<ChatMember> existingMember = chatMemberService.getMaybeMember(chatId, userEmailToAdd);

    if (existingMember.isPresent()) {
      ChatMember member = existingMember.get();
      if (member.isUserInGroup()) {
        throw new ChatMemberException("Пользователь уже в чате");
      }
      return chatMemberMapper.toDTO(chatMemberService.recreate(member));
    }

    User user = userService.getUserByEmail(userEmailToAdd);
    return chatMemberMapper.toDTO(chatMemberService.addUserToChat(chat, user));
  }

  @Override
  public ChatMemberDTO removeUser(Long chatId, String userEmailToRemove, String currentUserEmail) {
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
    return chatMemberMapper.toDTO(chatMemberService.removeMember(removingMember));
  }

  @Override
  public ChatMemberDTO mute(Long chatId, String userEmailToMute,
                            ZonedDateTime muteUntil, String clientEmail) {
    ChatMember client = chatMemberService.getMember(chatId, clientEmail);
    if(client.getRole().equals(MemberRole.MEMBER)) {
      throw new ChatMemberException(
          "У вас недостаточно прав, чтобы выдавать мут участникам этого чата");
    }
    return chatMemberMapper.toDTO(chatMemberService.mute(chatId, userEmailToMute, muteUntil));
  }

  @Override
  public ChatMemberDTO unmute(Long chatId, String userEmailToMute, String clientEmail) {
    ChatMember client = chatMemberService.getMember(chatId, clientEmail);
    if(client.getRole().equals(MemberRole.MEMBER)) {
      throw new ChatMemberException(
          "У вас недостаточно прав, чтобы снимать мут с участников этого чата");
    }
    return chatMemberMapper.toDTO(chatMemberService.unmute(chatId, userEmailToMute));
  }

  @Override
  public ChatMemberDTO leave(Long chatId, String userEmail) {
    return chatMemberMapper.toDTO(chatMemberService.leave(chatId, userEmail));
  }

  @Override
  public ChatMemberDTO changeRole(Long chatId, String email, MemberRole role, String clientName) {
    if(clientName.equals(email)) {
      throw new ChatMemberException("Вы не можете изменить свою роль в чате");
    }
    ChatMember member = chatMemberService.getMember(chatId, email);
    ChatMember client = chatMemberService.getMember(chatId, clientName);
    if(!client.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.ADMIN)) {
      throw new ChatMemberException("У вас нет прав, чтобы изменить роль этого участника чата");
    }
    return chatMemberMapper.toDTO(chatMemberService.changeRole(chatId, member, role));
  }
}
