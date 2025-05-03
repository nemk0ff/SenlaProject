package ru.senla.socialnetwork.services.chats.impl;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.chats.ChatMessageDao;
import ru.senla.socialnetwork.dto.chats.ChatMessageDTO;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMessageMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMessageException;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.ChatMessage;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.services.chats.ChatMessageService;
import ru.senla.socialnetwork.services.chats.CommonChatService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
  private final ChatMessageDao chatMessageDao;

  @Override
  public ChatMessageDTO send(Long chatId, String authorEmail, CreateMessageDTO request) {
    ChatMember member = commonChatService.getMember(chatId, authorEmail);

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

    ChatMessage savedMessage = chatMessageDao.saveOrUpdate(message);
    return savedMessage;
  }

  @Override
  public List<ChatMessageDTO> getAll(Long chatId) {
    List<ChatMessage> messages = chatMessageDao.findByChatId(chatId);
    return messages.stream()
        .map(chatMessageMapper::toDTO)
        .toList();
  }

  @Override
  public ChatMessageDTO pin(Long chatId, Long messageId) {
    ChatMessage message = getMessage(chatId, messageId);

    if (message.getIsPinned()) {
      throw new ChatMessageException("Это сообщение уже закреплено.");
    }
    message.setIsPinned(true);
    ChatMessage updatedMessage = chatMessageDao.saveOrUpdate(message);
    return chatMessageMapper.toDTO(updatedMessage);
  }

  @Override
  public ChatMessageDTO unpin(Long chatId, Long messageId) {
    ChatMessage message = getMessage(chatId, messageId);

    if (!message.getIsPinned()) {
      throw new ChatMessageException("Это сообщение не закреплено.");
    }

    message.setIsPinned(false);
    ChatMessage updatedMessage = chatMessageDao.saveOrUpdate(message);
    return chatMessageMapper.toDTO(updatedMessage);
  }

  @Override
  public void delete(Long chatId, Long messageId, String currentUserEmail) {
    ChatMessage message = getMessage(chatId, messageId);
    ChatMember member = commonChatService.getMember(chatId, currentUserEmail);

    if (!message.getAuthor().getUser().getEmail().equals(currentUserEmail)) {
      if (member.getRole() != MemberRole.ADMIN && member.getRole() != MemberRole.MODERATOR) {
        throw new ChatMessageException("Только автор, модератор или админ могут удалить сообщение");
      }
    }

    chatMessageDao.delete(message);
    log.info("Сообщение {} удалено пользователем {}", messageId, currentUserEmail);
  }

  private ChatMessage getMessage(Long chatId, Long messageId) {
    ChatMessage message = chatMessageDao.find(messageId)
        .orElseThrow(() -> new ChatMessageException("Сообщение не найдено"));

    if (!message.getAuthor().getChat().getId().equals(chatId)) {
      throw new ChatMessageException("Сообщение не принадлежит этому чату");
    }
    return message;
  }
}

