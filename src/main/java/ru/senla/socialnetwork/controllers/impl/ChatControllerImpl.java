package ru.senla.socialnetwork.controllers.impl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.ChatController;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateChatDTO;
import ru.senla.socialnetwork.services.chats.ChatService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/chats")
public class ChatControllerImpl implements ChatController {
  private final ChatService chatService;

  @Override
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or #request.creatorEmail() == authentication.name")
  public ResponseEntity<ChatDTO> createChat(@RequestBody @Valid CreateChatDTO request) {
    log.info("Создание нового чата пользователем {}", request.creatorEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(chatService.create(request));
  }
}
