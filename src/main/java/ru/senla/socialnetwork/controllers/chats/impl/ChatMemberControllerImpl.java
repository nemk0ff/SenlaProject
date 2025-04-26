package ru.senla.socialnetwork.controllers.chats.impl;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.chats.ChatMemberController;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.services.chats.ChatMemberService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/chats/{chatId}/members")
public class ChatMemberControllerImpl implements ChatMemberController {
  private final ChatMemberService chatMemberService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN') " +
      "OR @commonChatServiceImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<ChatMemberDTO> addMember(
      @PathVariable Long chatId,
      @RequestParam String userEmail) {
    return ResponseEntity.ok(chatMemberService.addUserToChat(chatId, userEmail));
  }

  @DeleteMapping("/{userEmail}")
  @PreAuthorize("(hasRole('ADMIN') " +
      "OR @commonChatServiceImpl.isChatMember(#chatId, authentication.name)) " +
      "AND authentication.name != userEmail")
  public ResponseEntity<Void> removeMember(
      @PathVariable Long chatId,
      @PathVariable String userEmail,
      Authentication auth) {
    chatMemberService.removeUserFromChat(chatId, userEmail, auth.getName());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{userEmail}/mute")
  @PreAuthorize("@commonChatServiceImpl.isChatMember(#chatId, auth.name)")
  public ResponseEntity<ChatMemberDTO> muteMember(
      @PathVariable Long chatId,
      @PathVariable String userEmail,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      ZonedDateTime muteUntil,
      Authentication auth) {
    return ResponseEntity.ok(chatMemberService.muteUser(
        chatId, userEmail, muteUntil, auth.getName()));
  }

  @PostMapping("/leave")
  @PreAuthorize("@commonChatServiceImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<Void> leaveChat(
      @PathVariable Long chatId,
      String userEmail) {
    chatMemberService.leaveChat(chatId, userEmail);
    return ResponseEntity.noContent().build();
  }
}