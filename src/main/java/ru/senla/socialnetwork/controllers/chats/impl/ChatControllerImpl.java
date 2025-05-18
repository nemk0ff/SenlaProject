package ru.senla.socialnetwork.controllers.chats.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.chats.ChatController;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.facades.chats.ChatFacade;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/chats")
public class ChatControllerImpl implements ChatController {
  private final ChatFacade chatFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> getUserChats(Authentication auth) {
    log.info("Запрос списка чатов для пользователя {}", auth.getName());
    List<ChatDTO> chats = chatFacade.getUserChats(auth.getName());
    log.info("Для пользователя {} найдено {} чатов", auth.getName(), chats.size());
    return ResponseEntity.ok(chats);
  }

  @Override
  @PostMapping("/group")
  public ResponseEntity<?> createGroupChat(
      @RequestBody @Valid CreateGroupChatDTO request,
      Authentication auth) {
    log.info("Создание группового чата пользователем {}. Участников: {}, название: '{}'",
        auth.getName(), request.membersEmails().size(), request.name());
    ChatDTO chat = chatFacade.create(request, auth.getName());
    log.info("Создан групповой чат: id={}, name='{}'", chat.id(), chat.name());
    return ResponseEntity.status(HttpStatus.CREATED).body(chat);
  }

  @Override
  @PostMapping("/personal")
  public ResponseEntity<?> createPersonalChat(
      @RequestParam @Email String participant,
      Authentication auth) {
    log.info("Создание персонального чата между {} и {}", auth.getName(), participant);
    ChatDTO chat = chatFacade.create(auth.getName(), participant);
    log.info("Создан персональный чат id={} между {} и {}", chat.id(), auth.getName(), participant);
    return ResponseEntity.status(HttpStatus.CREATED).body(chat);
  }

  @Override
  @DeleteMapping("/{chatId}")
  public ResponseEntity<?> deleteChat(
      @PathVariable Long chatId,
      Authentication auth) {
    String currentUser = auth.getName();
    log.info("Запрос на удаление чата id={}. пользователем id={}", chatId, currentUser);
    chatFacade.delete(chatId, auth.getName());
    log.info("Чат id={} успешно удален пользователем id={}", chatId, currentUser);
    return ResponseEntity.ok(new DeleteResponseDTO(
        "Чат успешно удалён",
        Map.of("chatId", chatId)));
  }

  @Override
  @GetMapping("/{chatId}")
  public ResponseEntity<?> getChat(
      @PathVariable Long chatId,
      Authentication auth) {
    log.info("Запрос информации о чате id={}", chatId);
    ChatDTO chat = chatFacade.get(chatId, auth.getName());
    log.info("Возвращена информация о чате id={}, тип={}, участников: {}",
        chatId, chat.isGroup(), chat.members().size());
    return ResponseEntity.ok(chat);
  }
}