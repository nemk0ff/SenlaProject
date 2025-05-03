package ru.senla.socialnetwork.facades.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.ChatMessageDTO;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;

public interface ChatMessageFacade {
  ChatMessageDTO send(Long chatId, String authorEmail, CreateMessageDTO request);

  List<ChatMessageDTO> getAll(Long chatId);

  ChatMessageDTO pin(Long chatId, Long messageId);

  ChatMessageDTO unpin(Long chatId, Long messageId);

  void delete(Long chatId, Long messageId, String currentUserEmail);
}
