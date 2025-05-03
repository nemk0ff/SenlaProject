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
import ru.senla.socialnetwork.facades.chats.ChatMemberFacade;
import ru.senla.socialnetwork.model.general.MemberRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/chats/{chatId}/members")
public class ChatMemberControllerImpl implements ChatMemberController {
  private final ChatMemberFacade chatMemberFacade;

  @Override
  @PostMapping
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<ChatMemberDTO> addMember(
      @PathVariable Long chatId,
      @RequestParam String userEmail) {
    return ResponseEntity.ok(chatMemberFacade.addUserToChat(chatId, userEmail));
  }

  @Override
  @DeleteMapping("/{email}")
  @PreAuthorize("authentication.name != #email")
  public ResponseEntity<Void> removeMember(
      @PathVariable Long chatId,
      @PathVariable String email,
      Authentication auth) {
    chatMemberFacade.removeUserFromChat(chatId, email, auth.getName());
    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping("/{email}/mute")
  @PreAuthorize("@chatMemberFacadeImpl.isChatAdminOrModerator(#chatId, #email)")
  public ResponseEntity<ChatMemberDTO> muteMember(
      @PathVariable Long chatId,
      @PathVariable String email,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      ZonedDateTime muteUntil) {
    return ResponseEntity.ok(chatMemberFacade.mute(chatId, email, muteUntil));
  }

  @Override
  @PostMapping("/{email}/unmute")
  @PreAuthorize("@chatMemberFacadeImpl.isChatAdminOrModerator(#chatId, #email)")
  public ResponseEntity<ChatMemberDTO> unmuteMember(
      @PathVariable Long chatId,
      @PathVariable String email) {
    return ResponseEntity.ok(chatMemberFacade.unmute(chatId, email));
  }

  @Override
  @PostMapping("/leave")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name)")
  public ResponseEntity<Void> leaveChat(
      @PathVariable Long chatId,
      String userEmail) {
    chatMemberFacade.leave(chatId, userEmail);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping("/role")
  @PreAuthorize("!authentication.name.equals(#email) " +
      "AND @chatMemberFacadeImpl.isChatAdmin(#chatId, #email)")
  public ResponseEntity<ChatMemberDTO> changeMemberRole(
      @PathVariable Long chatId,
      @RequestParam String email,
      @RequestParam MemberRole role) {
    return ResponseEntity.ok(chatMemberFacade.changeRole(chatId, email, role));
  }
}