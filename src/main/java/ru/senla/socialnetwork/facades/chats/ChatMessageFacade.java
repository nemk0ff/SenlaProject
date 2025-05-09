package ru.senla.socialnetwork.facades.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.ChatMessageDTO;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;

public interface ChatMessageFacade {
  ChatMessageDTO send(Long chatId, String authorEmail, CreateMessageDTO request);

  List<ChatMessageDTO> getAll(Long chatId, String clientEmail);

  ChatMessageDTO get(Long chatId, Long messageId, String clientEmail);

  List<ChatMessageDTO> getAnswers(Long chatId, Long messageId, String clientEmail);

  List<ChatMessageDTO> getPinned(Long chatId, String clientEmail);

  ChatMessageDTO pin(Long chatId, Long messageId, String clientEmail);

  ChatMessageDTO unpin(Long chatId, Long messageId, String clientEmail);

  void delete(Long chatId, Long messageId, String clientEmail);
}
