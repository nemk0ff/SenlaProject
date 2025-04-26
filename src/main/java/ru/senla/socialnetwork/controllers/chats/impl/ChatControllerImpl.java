package ru.senla.socialnetwork.controllers.chats.impl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.chats.ChatController;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;
import ru.senla.socialnetwork.services.chats.ChatService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/chats")
public class ChatControllerImpl implements ChatController {
  private final ChatService chatService;

  @Override
  @PostMapping("/group")
  @PreAuthorize("hasRole('ADMIN') or #request.creatorEmail() == authentication.name")
  public ResponseEntity<?> createChat(@RequestBody @Valid CreateGroupChatDTO request) {
    log.info("Создание группового чата");
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(chatService.create(request));
  }

  @Override
  @PostMapping("/personal")
  @PreAuthorize("hasRole('ADMIN') or #request.creatorEmail() == authentication.name")
  public ResponseEntity<?> createChat(@RequestBody @Valid CreatePersonalChatDTO request) {
    log.info("Создание персонального чата");
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(chatService.create(request));
  }

  @Override
  @DeleteMapping("/{chatId}")
  @PreAuthorize("hasRole('ADMIN') " +
      "or @chatMemberServiceImpl.isChatCreator(#chatId, authentication.name)")
  public ResponseEntity<?> deleteChat(@PathVariable Long chatId) {
    log.info("Удаление чата с ID {}", chatId);
    String chatName = chatService.getChat(chatId).name();
    chatService.deleteChat(chatId);
    return ResponseEntity.ok("Чат " + chatName + " удалён");
  }

  @Override
  @GetMapping("/{chatId}")
  public ResponseEntity<?> getChat(@PathVariable Long chatId) {
    log.info("Получение информации о чате с ID {}", chatId);
    return ResponseEntity.ok(chatService.getChat(chatId));
  }
}
