package ru.senla.socialnetwork.services.chats.impl;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMemberMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.CommonChatService;
import ru.senla.socialnetwork.services.common.CommonService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ChatMemberServiceImpl implements ChatMemberService {
  private static final int MAX_GROUP_CHAT_MEMBERS = 100;

  private final CommonChatService commonChatService;
  private final CommonService commonService;
  private final ChatMemberDao chatMemberDao;
  private final ChatMemberMapper chatMemberMapper;

  @Override
  public ChatMemberDTO addUserToChat(Long chatId, String userEmailToAdd) {
    Chat chat = commonChatService.getChat(chatId);

    if (!chat.getIsGroup()) {
      throw new ChatMemberException("Нельзя добавить участника в личный чат");
    }

    long membersCount = chatMemberDao.countByChatId(chatId);
    if (membersCount >= MAX_GROUP_CHAT_MEMBERS) {
      throw new ChatMemberException("Превышено максимальное количество участников в чате");
    }

    if (commonChatService.isChatMember(chatId, userEmailToAdd)) {
      throw new ChatMemberException("Пользователь уже в чате");
    }

    User userToAdd = commonService.getUserByEmail(userEmailToAdd);
    ChatMember newMember = ChatMember.builder()
        .chat(chat)
        .user(userToAdd)
        .role(MemberRole.MEMBER)
        .joinDate(ZonedDateTime.now())
        .build();

    ChatMember savedMember = chatMemberDao.saveOrUpdate(newMember);
    log.info("Пользователь {} добавлен в чат {}", userEmailToAdd, chatId);
    return chatMemberMapper.memberToDTO(savedMember);
  }

  @Override
  public void removeUserFromChat(Long chatId, String userEmailToRemove, String currentUserEmail) {
    Chat chat = commonChatService.getChat(chatId);

    if (!chat.getIsGroup()) {
      throw new ChatMemberException("Нельзя удалить участника из личного чата. Удалите весь чат.");
    }
    if (currentUserEmail.equals(userEmailToRemove)) {
      throw new ChatMemberException("Нельзя удалить самого себя. Используйте выход из чата");
    }

    ChatMember memberToRemove = commonChatService.getMember(chatId, userEmailToRemove);
    ChatMember currentMember = commonChatService.getMember(chatId, currentUserEmail);

    if (memberToRemove.getRole() == MemberRole.ADMIN) {
      throw new ChatMemberException("Нельзя удалить администратора");
    }
    if (memberToRemove.getRole() == MemberRole.MODERATOR &&
        currentMember.getRole() != MemberRole.ADMIN) {
      throw new ChatMemberException("Только администратор может удалить модератора");
    }
    if (currentMember.getRole() != MemberRole.ADMIN &&
        currentMember.getRole() != MemberRole.MODERATOR) {
      throw new ChatMemberException("Только администратор или модератор могут удалять участников");
    }

    chatMemberDao.delete(memberToRemove);
    log.info("Пользователь {} удален из чата {}", userEmailToRemove, chatId);
  }

  @Override
  public ChatMemberDTO muteUser(Long chatId, String userEmailToMute,
                                ZonedDateTime muteUntil, String currentUserEmail) {
    ChatMember memberToMute = commonChatService.getMember(chatId, userEmailToMute);
    ChatMember currentMember = commonChatService.getMember(chatId, currentUserEmail);

    if (currentMember.getRole() != MemberRole.ADMIN &&
        currentMember.getRole() != MemberRole.MODERATOR) {
      throw new ChatMemberException("Только администратор или модератор могут мьютить участников");
    }
    if (memberToMute.getRole() != MemberRole.MEMBER) {
      throw new ChatMemberException("Можно мьютить только обычных участников");
    }

    memberToMute.setMutedUntil(muteUntil);
    ChatMember updatedMember = chatMemberDao.saveOrUpdate(memberToMute);
    log.info("Пользователь {} замьючен в чате {} до {}", userEmailToMute, chatId, muteUntil);
    return chatMemberMapper.memberToDTO(updatedMember);
  }

  @Override
  public void leaveChat(Long chatId, String userEmail) {
    ChatMember member = commonChatService.getMember(chatId, userEmail);

    if (member.getRole() == MemberRole.ADMIN && countAdminsInChat(chatId) == 1) {
      throw new ChatMemberException("Нельзя покинуть чат, так как вы единственный админ. " +
          "Необходимо назначить какого-нибудь участника админом перед выходом из чата.");
    }

    chatMemberDao.delete(member);
    log.info("Пользователь {} покинул чат {}", userEmail, chatId);
  }


  @Override
  public ChatMemberDTO changeMemberRole(Long chatId, String userEmail,
                                        MemberRole newRole, String currentUserEmail) {
    ChatMember targetMember = commonChatService.getMember(chatId, userEmail);
    ChatMember currentMember = commonChatService.getMember(chatId, currentUserEmail);

    if (currentMember.getRole() == MemberRole.ADMIN) {
      targetMember.setRole(newRole);
    }
    else if (currentMember.getRole() == MemberRole.MODERATOR) {
      if (newRole != MemberRole.MODERATOR && newRole != MemberRole.MEMBER) {
        throw new ChatMemberException("Модератор может назначать только роль модератора или участника");
      }
      if (targetMember.getRole() == MemberRole.ADMIN) {
        throw new ChatMemberException("Модератор не может изменять роль администратора");
      }
      targetMember.setRole(newRole);
    }
    else {
      throw new ChatMemberException("Только администратор или модератор могут изменять роли");
    }

    ChatMember updatedMember = chatMemberDao.saveOrUpdate(targetMember);
    log.info("Роль пользователя {} в чате {} изменена на {}", userEmail, chatId, newRole);
    return chatMemberMapper.memberToDTO(updatedMember);
  }

  private long countAdminsInChat(Long chatId) {
    return chatMemberDao.countByChatIdAndRole(chatId, MemberRole.ADMIN);
  }
}
