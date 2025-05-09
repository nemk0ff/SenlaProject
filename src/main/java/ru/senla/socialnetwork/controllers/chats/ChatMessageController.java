package ru.senla.socialnetwork.controllers.chats;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;

public interface ChatMessageController {
  ResponseEntity<?> sendMessage(Long chatId, @Valid CreateMessageDTO request, Authentication auth);

  ResponseEntity<?> getMessages(Long chatId, Authentication auth);

  ResponseEntity<?> getMessage(Long chatId, Long messageId, Authentication auth);

  ResponseEntity<?> getAnswers(Long chatId, Long messageId, Authentication auth);

  ResponseEntity<?> getPinnedMessages(Long chatId, Authentication auth);

  ResponseEntity<?> pinMessage(Long chatId, Long messageId, Authentication auth);

  ResponseEntity<?> unpinMessage(Long chatId, Long messageId, Authentication auth);

  ResponseEntity<?> deleteMessage(Long chatId, Long messageId, Authentication auth);
}
