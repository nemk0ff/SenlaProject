package ru.senla.socialnetwork.facades.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.MessageResponseDTO;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;

public interface MessageFacade {
  MessageResponseDTO send(Long chatId, String authorEmail, MessageRequestDTO request);

  List<MessageResponseDTO> getAll(Long chatId, String clientEmail);

  MessageResponseDTO get(Long chatId, Long messageId, String clientEmail);

  List<MessageResponseDTO> getAnswers(Long chatId, Long messageId, String clientEmail);

  List<MessageResponseDTO> getPinned(Long chatId, String clientEmail);

  MessageResponseDTO pin(Long chatId, Long messageId, String clientEmail);

  MessageResponseDTO unpin(Long chatId, Long messageId, String clientEmail);

  void delete(Long chatId, Long messageId, String clientEmail);
}