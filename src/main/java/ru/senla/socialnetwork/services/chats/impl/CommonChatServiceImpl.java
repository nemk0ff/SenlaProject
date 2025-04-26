package ru.senla.socialnetwork.services.chats.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.services.chats.CommonChatService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommonChatServiceImpl implements CommonChatService {
  private final ChatDao chatDao;
  private final ChatMemberDao chatMemberDao;

  @Override
  @Transactional(readOnly = true)
  public Chat getChat(Long chatId) {
    return chatDao.findWithMembersAndUsers(chatId)
        .orElseThrow(() -> new EntityNotFoundException("Чат не найден."));
  }

  @Override
  public ChatMember getMember(Long chatId, String email) {
    return chatMemberDao.findByChatIdAndUserEmail(chatId, email)
        .orElseThrow(() -> new EntityNotFoundException("Участник не найден"));
  }

  @Override
  public boolean isChatMember(Long chatId, String userEmail) {
    return chatMemberDao.existsByChatIdAndUserEmail(chatId, userEmail);
  }

  @Override
  public boolean isAdmin(Long chatId, String email) {
    return getMember(chatId, email).getRole().equals(MemberRole.ADMIN);
  }

  @Override
  public boolean isModerator(Long chatId, String email) {
    return getMember(chatId, email).getRole().equals(MemberRole.MODERATOR);
  }
}
