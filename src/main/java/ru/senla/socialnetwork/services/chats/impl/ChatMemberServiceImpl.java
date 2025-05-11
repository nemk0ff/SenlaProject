package ru.senla.socialnetwork.services.chats.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatMemberService;

@Slf4j
@Service
@AllArgsConstructor
public class ChatMemberServiceImpl implements ChatMemberService {
  private static final int MAX_CHAT_SIZE = 100;
  private static final int MAX_MODERATORS_NUMBER = 10;
  private static final int MAX_ADMINS_NUMBER = 3;

  private final ChatMemberDao chatMemberDao;

  @Override
  public ChatMember addUserToChat(Chat chat, User user) {
    if (!chat.getIsGroup()) {
      throw new ChatMemberException("Нельзя добавить участника в личный чат");
    }

    long membersCount = chatMemberDao.countByChatId(chat.getId());
    if (membersCount >= MAX_CHAT_SIZE) {
      throw new ChatMemberException("Превышено максимальное количество участников в чате");
    }

    ChatMember newMember = ChatMember.builder()
        .chat(chat)
        .user(user)
        .role(MemberRole.MEMBER)
        .joinDate(ZonedDateTime.now())
        .build();
    ChatMember savedMember = chatMemberDao.saveOrUpdate(newMember);
    log.info("В чат {} добавлен участник {}", chat.getId(), newMember);
    return savedMember;
  }

  @Override
  public ChatMember mute(Long chatId, String userEmailToMute, ZonedDateTime muteUntil) {
    log.info("Установка mute на пользователя {} в чате {}", userEmailToMute, chatId);

    ChatMember memberToMute = getMember(chatId, userEmailToMute);
    if (memberToMute.getRole() != MemberRole.MEMBER) {
      throw new ChatMemberException("Можно мьютить только обычных участников");
    }
    memberToMute.setMutedUntil(muteUntil);
    ChatMember updatedMember = chatMemberDao.saveOrUpdate(memberToMute);
    log.info("Пользователь {} замьючен в чате {} до {}", userEmailToMute, chatId, muteUntil);
    return updatedMember;
  }

  @Override
  public ChatMember unmute(Long chatId, String userEmail) {
    log.info("Снятие mute с пользователя {} в чате {}", userEmail, chatId);
    ChatMember memberToMute = getMember(chatId, userEmail);
    if (memberToMute.getMutedUntil().isBefore(ZonedDateTime.now())) {
      throw new ChatMemberException("Пользователь не является замьюченным");
    }
    memberToMute.setMutedUntil(ZonedDateTime.now());
    ChatMember updatedMember = chatMemberDao.saveOrUpdate(memberToMute);
    log.info("Пользователь {} размьючен в чате {}", userEmail, chatId);
    return updatedMember;
  }

  @Override
  public ChatMember leave(Long chatId, String userEmail) {
    ChatMember member = getMember(chatId, userEmail);
    if (member.getRole() == MemberRole.ADMIN && countAdminsInChat(chatId) == 1) {
      throw new ChatMemberException("Нельзя покинуть чат, так как вы единственный админ. " +
          "Необходимо назначить какого-нибудь участника админом перед выходом из чата.");
    }
    member.setLeaveDate(ZonedDateTime.now());
    return chatMemberDao.saveOrUpdate(member);
  }


  @Override
  public ChatMember changeRole(Long chatId, ChatMember member, MemberRole newRole) {
    if (member.getRole().equals(newRole)) {
      return member;
    }

    if (newRole.equals(MemberRole.ADMIN) && (countAdminsInChat(chatId) > MAX_ADMINS_NUMBER)) {
      throw new ChatMemberException("В чате слишком много админов, роль пользователя не изменена");
    } else if (newRole.equals(MemberRole.MODERATOR)
        && (countModeratorsInChat(chatId) > MAX_MODERATORS_NUMBER)) {
      throw new ChatMemberException("В чате слишком много модераторов, роль пользователя не " +
          "изменена");
    }
    member.setRole(newRole);

    ChatMember updatedMember = chatMemberDao.saveOrUpdate(member);
    log.info("Роль пользователя {} в чате {} изменена на {}", member.getUser().getEmail(), chatId, newRole);
    return updatedMember;
  }

  @Override
  public ChatMember getMember(Long chatId, String email) {
    ChatMember member = chatMemberDao.findActiveByChatIdAndUserEmail(chatId, email)
        .orElseThrow(() -> new EntityNotFoundException(
            "Участник " + email + " чата id= " + chatId + " не найден"));
    if(!member.isUserInGroup()) {
      throw new ChatMemberException(
          "Пользователь " + email + " не является участником чата с момента " + member.getLeaveDate());
    }
    return member;
  }

  @Override
  public Optional<ChatMember> getMaybeMember(Long chatId, String email) {
    return chatMemberDao.findByChatIdAndUserEmail(chatId, email);
  }

  @Override
  public List<ChatMember> getMembers(Long chatId) {
    return chatMemberDao.findMembersByChatId(chatId);
  }

  @Override
  public ChatMember removeMember(ChatMember member) {
    member.setLeaveDate(ZonedDateTime.now());
    return chatMemberDao.saveOrUpdate(member);
  }

  @Override
  public void deleteMember(ChatMember member) {
    chatMemberDao.delete(member);
  }

  @Override
  public boolean isChatMember(Long chatId, String userEmail) {
    log.info("Проверяем, является ли {} участником чата {}...", userEmail, chatId);
    boolean result = getMember(chatId, userEmail).isUserInGroup();
    log.info("Выражение '{} - участник чата {}' имеет значение {}", userEmail, chatId, result);
    return result;
  }

  @Override
  public ChatMember recreate(ChatMember member) {
    member.setJoinDate(ZonedDateTime.now());
    return chatMemberDao.saveOrUpdate(member);
  }

  @Override
  public void saveMembers(List<ChatMember> members) {
    chatMemberDao.saveAll(members);
  }

  private long countAdminsInChat(Long chatId) {
    return chatMemberDao.countByChatIdAndRole(chatId, MemberRole.ADMIN);
  }

  private long countModeratorsInChat(Long chatId) {
    return chatMemberDao.countByChatIdAndRole(chatId, MemberRole.MODERATOR);
  }
}
