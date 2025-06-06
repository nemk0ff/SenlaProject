package ru.senla.socialnetwork.controllers.chats.impl;

import jakarta.validation.constraints.Email;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.chats.ChatMemberController;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.facades.chats.ChatMemberFacade;
import ru.senla.socialnetwork.model.MemberRole;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/chats/{chatId}/members")
public class ChatMemberControllerImpl implements ChatMemberController {
  private final ChatMemberFacade chatMemberFacade;

  @Override
  @PostMapping
  public ResponseEntity<?> addMember(
      @PathVariable Long chatId,
      @RequestParam @Email String email,
      Authentication auth) {
    log.info("Добавление участника {} в чат {} пользователем {}",
        email, chatId, auth.getName());
    ChatMemberDTO result = chatMemberFacade.addUserToChat(chatId, email, auth.getName());
    log.info("Участник {} успешно добавлен в чат {}", email, chatId);
    return ResponseEntity.ok(result);
  }

  @Override
  @DeleteMapping
  public ResponseEntity<?> removeMember(
      @PathVariable Long chatId,
      @RequestParam @Email String email,
      Authentication auth) {
    log.info("Удаление участника {} из чата {} пользователем {}",
        email, chatId, auth.getName());
    ChatMemberDTO result = chatMemberFacade.removeUser(chatId, email, auth.getName());
    log.info("Участник {} успешно удален из чата {}", email, chatId);
    return ResponseEntity.ok(result);
  }

  @Override
  @PostMapping("/mute")
  public ResponseEntity<?> muteMember(
      @PathVariable Long chatId,
      @RequestParam @Email String email,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      ZonedDateTime muteUntil,
      Authentication auth) {
    log.info("Мут участника {} в чате {} до {} пользователем {}",
        email, chatId, muteUntil, auth.getName());
    ChatMemberDTO result = chatMemberFacade.mute(chatId, email, muteUntil, auth.getName());
    log.info("Участник {} успешно замьючен до {}", email, muteUntil);
    return ResponseEntity.ok(result);
  }

  @Override
  @PostMapping("/unmute")
  public ResponseEntity<?> unmuteMember(
      @PathVariable Long chatId,
      @RequestParam @Email String email,
      Authentication auth) {
    log.info("Размут участника {} в чате {} (инициатор: {})", email, chatId, auth.getName());
    ChatMemberDTO result = chatMemberFacade.unmute(chatId, email, auth.getName());
    log.info("Участник {} успешно размьючен", email);
    return ResponseEntity.ok(result);
  }

  @Override
  @DeleteMapping("/leave")
  public ResponseEntity<?> leaveChat(
      @PathVariable Long chatId,
      Authentication auth) {
    log.info("Выход пользователя {} из чата {}", auth.getName(), chatId);
    ChatMemberDTO chatMember = chatMemberFacade.leave(chatId, auth.getName());
    log.info("Пользователь {} вышел из чата {}", auth.getName(), chatId);
    return ResponseEntity.ok(chatMember);
  }

  @Override
  @PostMapping("/role")
  public ResponseEntity<?> changeMemberRole(
      @PathVariable Long chatId,
      @RequestParam @Email String email,
      @RequestParam MemberRole role,
      Authentication auth) {
    log.info("Изменение роли участника {} в чате {} на {} пользователем {}",
        email, chatId, role, auth.getName());
    ChatMemberDTO result = chatMemberFacade.changeRole(chatId, email, role, auth.getName());
    log.info("Роль участника {} изменена на {}", email, role);
    return ResponseEntity.ok(result);
  }
}