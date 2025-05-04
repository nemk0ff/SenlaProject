package ru.senla.socialnetwork.services.chats.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
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
  public ChatMember addUserToChat(Chat chat, ChatMember newMember) {
    if (!chat.getIsGroup()) {
      throw new ChatMemberException("Нельзя добавить участника в личный чат");
    }

    long membersCount = chatMemberDao.countByChatId(chat.getId());
    if (membersCount >= MAX_CHAT_SIZE) {
      throw new ChatMemberException("Превышено максимальное количество участников в чате");
    }

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
  public void leave(Long chatId, String userEmail) {
    ChatMember member = getMember(chatId, userEmail);
    if (member.getRole() == MemberRole.ADMIN && countAdminsInChat(chatId) == 1) {
      throw new ChatMemberException("Нельзя покинуть чат, так как вы единственный админ. " +
          "Необходимо назначить какого-нибудь участника админом перед выходом из чата.");
    }

    chatMemberDao.delete(member);
    log.info("Пользователь {} покинул чат {}", userEmail, chatId);
  }


  @Override
  public ChatMember changeRole(Long chatId, String userEmail,
                               MemberRole newRole) {
    ChatMember member = getMember(chatId, userEmail);
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
    log.info("Роль пользователя {} в чате {} изменена на {}", userEmail, chatId, newRole);
    return updatedMember;
  }

  @Override
  public ChatMember getMember(Long chatId, String email) {
    return chatMemberDao.findByChatIdAndUserEmail(chatId, email)
        .orElseThrow(() -> new EntityNotFoundException("Участник не найден"));
  }

  @Override
  public List<ChatMember> getMembers(Long chatId) {
    return chatMemberDao.findMembersByChatId(chatId);
  }

  @Override
  public void removeMember(ChatMember member) {
    chatMemberDao.delete(member);
  }

  @Override
  public boolean isChatMember(Long chatId, String userEmail) {
    return chatMemberDao.existsByChatIdAndUserEmail(chatId, userEmail);
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
