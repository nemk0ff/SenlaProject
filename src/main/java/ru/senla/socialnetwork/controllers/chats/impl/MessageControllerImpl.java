package ru.senla.socialnetwork.controllers.chats.impl;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.chats.MessageController;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.chats.MessageResponseDTO;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.facades.chats.MessageFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/chats/{chatId}/messages")
public class MessageControllerImpl implements MessageController {
  private final MessageFacade messageFacade;

  @Override
  @PostMapping
  public ResponseEntity<?> sendMessage(
      @PathVariable Long chatId,
      @RequestBody @Valid MessageRequestDTO request,
      Authentication auth) {
    log.info("Пользователь {} отправляет сообщение в чат {}", auth.getName(), chatId);
    MessageResponseDTO message = messageFacade.send(chatId, auth.getName(), request);
    log.info("Сообщение ID {} успешно отправлено в чат {} пользователем {}",
        message.id(), chatId, auth.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @Override
  @GetMapping
  public ResponseEntity<?> getMessages(
      @PathVariable Long chatId,
      Authentication auth) {
    log.info("Запрос сообщений из чата {} пользователем {}", chatId, auth.getName());
    List<MessageResponseDTO> messages = messageFacade.getAll(chatId, auth.getName());
    log.info("Возвращено {} сообщений из чата {}", messages.size(), chatId);
    return ResponseEntity.ok(messages);
  }

  @Override
  @GetMapping("/{messageId}")
  public ResponseEntity<?> getMessage(
      @PathVariable Long chatId,
      @PathVariable Long messageId,
      Authentication auth) {
    log.info("Запрос сообщения id={} из чата {} пользователем {}", messageId, chatId, auth.getName());
    MessageResponseDTO message = messageFacade.get(chatId, messageId, auth.getName());
    log.info("Сообщение id={} найдено в чате {}", messageId, chatId);
    return ResponseEntity.ok(message);
  }

  @Override
  @GetMapping("/{messageId}/answers")
  public ResponseEntity<?> getAnswers(
      @PathVariable Long chatId,
      @PathVariable Long messageId,
      Authentication auth) {
    log.info("Запрос ответов на сообщение id={} из чата {} пользователем {}",
        messageId, chatId, auth.getName());
    List<MessageResponseDTO> answers = messageFacade.getAnswers(chatId, messageId, auth.getName());
    log.info("Найдено {} ответов на сообщение id={}", answers.size(), messageId);
    return ResponseEntity.ok(answers);
  }

  @Override
  @GetMapping("/pinned")
  public ResponseEntity<?> getPinnedMessages(
      @PathVariable Long chatId,
      Authentication auth) {
    log.info("Запрос закрепленных сообщений из чата {} пользователем {}", chatId, auth.getName());
    List<MessageResponseDTO> pinnedMessages = messageFacade.getPinned(chatId, auth.getName());
    log.info("Найдено {} закрепленных сообщений в чате {}", pinnedMessages.size(), chatId);
    return ResponseEntity.ok(pinnedMessages);
  }

  @Override
  @PatchMapping("/{messageId}/pin")
  public ResponseEntity<?> pinMessage(
      @PathVariable Long chatId,
      @PathVariable Long messageId,
      Authentication auth) {
    log.info("Пользователь {} закрепляет сообщение id={} в чате {}", auth.getName(), messageId,
        chatId);
    MessageResponseDTO message = messageFacade.pin(chatId, messageId, auth.getName());
    log.info("Сообщение id={} успешно закреплено в чате {}", messageId, chatId);
    return ResponseEntity.ok(message);
  }

  @Override
  @DeleteMapping("/{messageId}/pin")
  public ResponseEntity<?> unpinMessage(
      @PathVariable Long chatId,
      @PathVariable Long messageId,
      Authentication auth) {
    log.info("Пользователь {} открепляет сообщение id={} в чате {}", auth.getName(), messageId, chatId);
    MessageResponseDTO message = messageFacade.unpin(chatId, messageId, auth.getName());
    log.info("Сообщение id={} успешно откреплено в чате {}", messageId, chatId);
    return ResponseEntity.ok(message);
  }

  @Override
  @DeleteMapping("/{messageId}")
  public ResponseEntity<?> deleteMessage(
      @PathVariable Long chatId,
      @PathVariable Long messageId,
      Authentication auth) {
    log.info("Пользователь {} удаляет сообщение id={} из чата {}", auth.getName(), messageId, chatId);
    messageFacade.delete(chatId, messageId, auth.getName());
    log.info("Сообщение id={} успешно удалено из чата id={}", messageId, chatId);
    return ResponseEntity.ok(new DeleteResponseDTO(
        "Сообщение успешно удалено",
        Map.of(
            "chatId", chatId,
            "messageId", messageId)));
  }
}
