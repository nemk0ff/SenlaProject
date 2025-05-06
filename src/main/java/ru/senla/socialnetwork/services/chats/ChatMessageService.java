package ru.senla.socialnetwork.services.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.ChatMessage;

public interface ChatMessageService {
  ChatMessage send(ChatMember member, CreateMessageDTO request);

  List<ChatMessage> getAll(Long chatId);

  ChatMessage get(Long chatId, Long messageId);

  List<ChatMessage> getAnswers(Long chatId, Long messageId);

  List<ChatMessage> getPinned(Long chatId);

  ChatMessage pin(Long chatId, Long messageId);

  ChatMessage unpin(Long chatId, Long messageId);

  void delete(ChatMessage message);
}
