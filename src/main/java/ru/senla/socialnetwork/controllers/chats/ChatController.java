package ru.senla.socialnetwork.controllers.chats;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;

public interface ChatController {
  ResponseEntity<?> getUserChats(String email);

  ResponseEntity<?> createChat(@Valid CreateGroupChatDTO request);

  ResponseEntity<?> createChat(@Valid CreatePersonalChatDTO request);

  ResponseEntity<?> deleteChat(@PathVariable Long chatId);

  ResponseEntity<?> getChat(@PathVariable Long chatId);
}
