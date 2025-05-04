package ru.senla.socialnetwork.controllers.chats.impl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.chats.ChatMessageController;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;
import ru.senla.socialnetwork.facades.chats.ChatMessageFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/chats/{chatId}/messages")
public class ChatMessageControllerImpl implements ChatMessageController {
  private final ChatMessageFacade chatMessageFacade;

  @Override
  @PostMapping
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<?> sendMessage(
      @PathVariable Long chatId,
      @RequestBody @Valid CreateMessageDTO request) {
    log.info("Отправка сообщения в чат {} пользователем {}", chatId, request.senderEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(chatMessageFacade.send(chatId, request.senderEmail(), request));
  }

  @Override
  @GetMapping
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<?> getMessages(@PathVariable Long chatId) {
    log.info("Получение сообщений из чата {}", chatId);
    return ResponseEntity.ok(chatMessageFacade.getAll(chatId));
  }

  @Override
  @GetMapping("/{messageId}")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<?> getMessage(@PathVariable Long chatId,
                                      @PathVariable Long messageId) {
    log.info("Получение сообщения {} из чата {}", messageId, chatId);
    return ResponseEntity.ok(chatMessageFacade.get(chatId, messageId));
  }

  @Override
  @GetMapping("/{messageId}/answers")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<?> getAnswers(@PathVariable Long chatId,
                                      @PathVariable Long messageId) {
    log.info("Получение ответов на сообщение {} из чата {}", messageId, chatId);
    return ResponseEntity.ok(chatMessageFacade.getAnswers(chatId, messageId));
  }

  @Override
  @GetMapping("/pinned")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<?> getPinnedMessages(@PathVariable Long chatId) {
    log.info("Получение закрепленных сообщений из чата {}", chatId);
    return ResponseEntity.ok(chatMessageFacade.getPinned(chatId));
  }

  @Override
  @PostMapping("/{messageId}/pin")
  @PreAuthorize("@chatMemberFacadeImpl.isChatAdminOrModerator(#chatId, authentication.name)")
  public ResponseEntity<?> pinMessage(
      @PathVariable Long chatId,
      @PathVariable Long messageId) {
    log.info("Закрепление сообщения {} в чате {}", messageId, chatId);
    return ResponseEntity.ok(chatMessageFacade.pin(chatId, messageId));
  }

  @Override
  @DeleteMapping("/{messageId}/pin")
  @PreAuthorize("@chatMemberFacadeImpl.isChatAdminOrModerator(#chatId, authentication.name)")
  public ResponseEntity<?> unpinMessage(
      @PathVariable Long chatId,
      @PathVariable Long messageId) {
    log.info("Открепление сообщения {} в чате {}", messageId, chatId);
    return ResponseEntity.ok(chatMessageFacade.unpin(chatId, messageId));
  }

  @Override
  @DeleteMapping("/{messageId}")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<?> deleteMessage(
      @PathVariable Long chatId,
      @PathVariable Long messageId,
      Authentication authentication) {
    log.info("Удаление сообщения {} в чате {} пользователем {}",
        messageId, chatId, authentication.getName());
    chatMessageFacade.delete(chatId, messageId, authentication.getName());
    return ResponseEntity.noContent().build();
  }
}
