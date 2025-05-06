package ru.senla.socialnetwork.services.chats.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.services.chats.ChatService;

@Slf4j
@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final ChatDao chatDao;

  @Override
  public List<Chat> getAllByUser(Long userId) {
    List<Chat> chats = chatDao.findAllByUserId(userId);
    if (chats.isEmpty()) {
      throw new ChatMemberException("Пользователь " + userId + " не состоит в чатах");
    }
    return chats;
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

    log.info("Создан групповой чат {} пользователем {}", request.name(), request.creatorEmail());
    return chat;
  }

  @Override
  public Chat create(CreatePersonalChatDTO request, String chatName) {
    if (chatDao.existsByMembers(request.creatorEmail(), request.friendEmail())) {
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
