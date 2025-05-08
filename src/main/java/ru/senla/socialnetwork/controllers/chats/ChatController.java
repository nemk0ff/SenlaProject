package ru.senla.socialnetwork.controllers.chats;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;

public interface ChatController {
  ResponseEntity<?> getUserChats(Authentication auth);

  ResponseEntity<?> createGroupChat(@Valid CreateGroupChatDTO request, Authentication auth);

  ResponseEntity<?> createPersonalChat(String participantEmail, Authentication auth);

  ResponseEntity<?> deleteChat(@PathVariable Long chatId, Authentication auth);

  ResponseEntity<?> getChat(@PathVariable Long chatId, Authentication auth);
}
