package ru.senla.socialnetwork.controllers.chats;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;

public interface ChatMessageController {
  ResponseEntity<?> sendMessage(Long chatId, @Valid CreateMessageDTO request);

  ResponseEntity<?> getMessages(Long chatId);

  ResponseEntity<?> getMessage(Long chatId, Long messageId);

  ResponseEntity<?> getAnswers(Long chatId, Long messageId);

  ResponseEntity<?> getPinnedMessages(Long chatId);

  ResponseEntity<?> pinMessage(Long chatId, Long messageId);

  ResponseEntity<?> unpinMessage(Long chatId, Long messageId);

  ResponseEntity<?> deleteMessage(Long chatId, Long messageId, Authentication authentication);
}
