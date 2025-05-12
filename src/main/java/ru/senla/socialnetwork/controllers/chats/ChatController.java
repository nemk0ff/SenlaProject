package ru.senla.socialnetwork.controllers.chats;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;

public interface ChatController {
  ResponseEntity<?> getUserChats(Authentication auth);

  ResponseEntity<?> createGroupChat(@Valid CreateGroupChatDTO request, Authentication auth);

  ResponseEntity<?> createPersonalChat(@Email String participantEmail, Authentication auth);

  ResponseEntity<?> deleteChat(Long chatId, Authentication auth);

  ResponseEntity<?> getChat(Long chatId, Authentication auth);
}
