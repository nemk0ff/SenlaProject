package ru.senla.socialnetwork.services.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.ChatMessageDTO;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;

public interface ChatMessageService {
  ChatMessageDTO sendMessage(Long chatId, String authorEmail, CreateMessageDTO request);

  List<ChatMessageDTO> getMessages(Long chatId);

  ChatMessageDTO pinMessage(Long chatId, Long messageId);

  ChatMessageDTO unpinMessage(Long chatId, Long messageId);
}
