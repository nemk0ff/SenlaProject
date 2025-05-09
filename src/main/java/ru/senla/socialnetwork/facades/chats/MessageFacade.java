package ru.senla.socialnetwork.facades.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.MessageDTO;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;

public interface MessageFacade {
  MessageDTO send(Long chatId, String authorEmail, CreateMessageDTO request);

  List<MessageDTO> getAll(Long chatId, String clientEmail);

  MessageDTO get(Long chatId, Long messageId, String clientEmail);

  List<MessageDTO> getAnswers(Long chatId, Long messageId, String clientEmail);

  List<MessageDTO> getPinned(Long chatId, String clientEmail);

  MessageDTO pin(Long chatId, Long messageId, String clientEmail);

  MessageDTO unpin(Long chatId, Long messageId, String clientEmail);

  void delete(Long chatId, Long messageId, String clientEmail);
}
