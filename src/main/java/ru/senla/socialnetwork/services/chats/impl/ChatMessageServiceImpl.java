package ru.senla.socialnetwork.services.chats.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.chats.ChatMessageDao;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.exceptions.chats.ChatMessageException;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.ChatMessage;
import ru.senla.socialnetwork.services.chats.ChatMessageService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
  private final ChatMessageDao chatMessageDao;

  @Override
  public ChatMessage send(ChatMember member, CreateMessageDTO request) {
    if (member.getMutedUntil() != null && member.getMutedUntil().isAfter(ZonedDateTime.now())) {
      throw new ChatMessageException("Вы замьючены до " + member.getMutedUntil());
    }

    ChatMessage message = ChatMessage.builder()
        .author(member)
        .body(request.body())
        .createdAt(ZonedDateTime.now())
        .isPinned(false)
        .build();

    if (request.replyToId() != null) {
      ChatMessage replyTo = chatMessageDao.find(request.replyToId())
          .orElseThrow(() -> new ChatMessageException("Сообщение для ответа не найдено"));
      message.setReplyTo(replyTo);
    }
    return chatMessageDao.saveOrUpdate(message);
  }

  @Override
  public List<ChatMessage> getAll(Long chatId) {
    List<ChatMessage> messages = chatMessageDao.findByChatId(chatId);
    if(messages.isEmpty()){
      throw new ChatMemberException("Чат пуст, отправьте сообщение первым");
    }
    return messages;
  }

  @Override
  public ChatMessage get(Long chatId, Long messageId) {
    ChatMessage message = chatMessageDao.find(messageId)
        .orElseThrow(() -> new ChatMessageException("Сообщение не найдено"));

    if (!message.getAuthor().getChat().getId().equals(chatId)) {
      throw new ChatMessageException("Сообщение не принадлежит этому чату");
    }
    return message;
  }

  @Override
  public List<ChatMessage> getAnswers(Long chatId, Long messageId) {
    List<ChatMessage> messages = chatMessageDao.findAnswers(chatId, messageId);
    if(messages.isEmpty()){
      throw new ChatMemberException("Ответов на сообщение не найдено");
    }
    return messages;
  }

  @Override
  public List<ChatMessage> getPinned(Long chatId) {
    List<ChatMessage> messages = chatMessageDao.findPinnedByChatId(chatId);
    if(messages.isEmpty()){
      throw new ChatMemberException("В чате нет закреплённых сообщений");
    }
    return messages;
  }

  @Override
  public ChatMessage pin(Long chatId, Long messageId) {
    ChatMessage message = get(chatId, messageId);

    if (message.getIsPinned()) {
      throw new ChatMessageException("Это сообщение уже закреплено.");
    }
    message.setIsPinned(true);
    return chatMessageDao.saveOrUpdate(message);
  }

  @Override
  public ChatMessage unpin(Long chatId, Long messageId) {
    ChatMessage message = get(chatId, messageId);

    if (!message.getIsPinned()) {
      throw new ChatMessageException("Это сообщение не закреплено.");
    }
    message.setIsPinned(false);
    return chatMessageDao.saveOrUpdate(message);
  }

  @Override
  public void delete(ChatMessage message) {
    chatMessageDao.delete(message);
  }
}

