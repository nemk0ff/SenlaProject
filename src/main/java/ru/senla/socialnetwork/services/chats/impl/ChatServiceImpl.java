package ru.senla.socialnetwork.services.chats.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.services.chats.ChatService;

@Slf4j
@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final ChatDao chatDao;

  @Override
  public List<Chat> getAllByUser(Long userId) {
    return chatDao.findAllChatsByUserId(userId);
  }

  @Override
  public Chat create(CreateGroupChatDTO request) {
    if (request.membersEmails().isEmpty()) {
      throw new ChatException("Групповой чат должен иметь хотя бы одного участника");
    }
    Chat chat = chatDao.saveOrUpdate(Chat.builder()
        .name(request.name())
        .isGroup(true)
        .createdAt(ZonedDateTime.now())
        .build());

    log.info("Создан групповой чат {}", request.name());
    return chat;
  }

  @Override
  public Chat create(String firstEmail, String secondEmail, String chatName) {
    if (chatDao.existsByMembers(firstEmail, secondEmail)) {
      throw new ChatException("Личный чат " + chatName + " уже существует");
    }
    Chat chat = chatDao.saveOrUpdate(Chat.builder()
        .name(chatName)
        .isGroup(false)
        .createdAt(ZonedDateTime.now())
        .build());

    log.info("Создан личный чат {}", chatName);
    return chat;
  }

  @Override
  public void delete(Chat chat) {
    chatDao.delete(chat);
  }

  @Override
  public Chat get(Long chatId) {
    return chatDao.findWithMembersAndUsers(chatId)
        .orElseThrow(() -> new EntityNotFoundException("Чат не найден."));
  }
}
