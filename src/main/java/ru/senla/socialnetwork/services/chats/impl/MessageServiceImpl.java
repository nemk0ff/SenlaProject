package ru.senla.socialnetwork.services.chats.impl;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.chats.MessageDao;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.exceptions.chats.MessageException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.Message;
import ru.senla.socialnetwork.services.chats.MessageService;

@Slf4j
@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
  private final MessageDao messageDao;

  @Override
  public Message send(ChatMember member, MessageRequestDTO request, Chat chat) {
    if (member.getMutedUntil() != null && member.getMutedUntil().isAfter(ZonedDateTime.now())) {
      throw new MessageException("Вы замьючены до " + member.getMutedUntil());
    }

    Message message = Message.builder()
        .chat(chat)
        .author(member.getUser())
        .body(request.body())
        .createdAt(ZonedDateTime.now())
        .isPinned(false)
        .build();

    if (request.replyToId() != null) {
      Message replyTo = messageDao.findByIdAndChatId(request.replyToId(), chat.getId())
          .orElseThrow(() -> new MessageException("Сообщение для ответа не найдено"));
      message.setReplyTo(replyTo);
    }
    return messageDao.saveOrUpdate(message);
  }

  @Override
  public List<Message> getAll(Long chatId) {
    return messageDao.findByChatId(chatId);
  }

  @Override
  public Message get(Long chatId, Long messageId) {
    Message message = messageDao.find(messageId)
        .orElseThrow(() -> new MessageException("Сообщение не найдено"));

    if (!message.getChat().getId().equals(chatId)) {
      throw new MessageException("Сообщение не принадлежит этому чату");
    }
    return message;
  }

  @Override
  public List<Message> getAnswers(Long chatId, Long messageId) {
    return messageDao.findAnswers(chatId, messageId);
  }

  @Override
  public List<Message> getPinned(Long chatId) {
    List<Message> messages = messageDao.findPinnedByChatId(chatId);
    if(messages.isEmpty()){
      throw new ChatMemberException("В чате нет закреплённых сообщений");
    }
    return messages;
  }

  @Override
  public Message pin(Long chatId, Long messageId) {
    Message message = get(chatId, messageId);

    if (message.getIsPinned()) {
      throw new MessageException("Это сообщение уже закреплено.");
    }
    message.setIsPinned(true);
    return messageDao.saveOrUpdate(message);
  }

  @Override
  public Message unpin(Long chatId, Long messageId) {
    Message message = get(chatId, messageId);

    if (!message.getIsPinned()) {
      throw new MessageException("Это сообщение не закреплено.");
    }
    message.setIsPinned(false);
    return messageDao.saveOrUpdate(message);
  }

  @Override
  public void delete(Message message) {
    messageDao.delete(message);
  }
}

