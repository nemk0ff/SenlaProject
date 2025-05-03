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
import ru.senla.socialnetwork.facade.chats.ChatMemberFacade;
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
  @DeleteMapping("/{userEmail}")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, authentication.name) " +
      "AND authentication.name != userEmail")
  public ResponseEntity<Void> removeMember(
      @PathVariable Long chatId,
      @PathVariable String userEmail,
      Authentication auth) {
    chatMemberFacade.removeUserFromChat(chatId, userEmail, auth.getName());
    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping("/{userEmail}/mute")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, auth.name)")
  public ResponseEntity<ChatMemberDTO> muteMember(
      @PathVariable Long chatId,
      @PathVariable String userEmail,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      ZonedDateTime muteUntil,
      Authentication auth) {
    return ResponseEntity.ok(chatMemberFacade.mute(chatId, userEmail, muteUntil, auth.getName()));
  }

  @Override
  @PostMapping("/{userEmail}/unmute")
  @PreAuthorize("@chatMemberFacadeImpl.isChatMember(#chatId, auth.name)")
  public ResponseEntity<ChatMemberDTO> unmuteMember(
      @PathVariable Long chatId,
      @PathVariable String userEmail,
      Authentication auth) {
    return ResponseEntity.ok(chatMemberFacade.unmute(chatId, userEmail, auth.getName()));
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
  @PreAuthorize("!authentication.name.equals(#email)")
  public ResponseEntity<ChatMemberDTO> changeMemberRole(
      @PathVariable Long chatId,
      @RequestParam String email,
      @RequestParam MemberRole role,
      Authentication auth) {
    return ResponseEntity.ok(chatMemberFacade.changeRole(chatId, email, role, auth.getName()));
  }
}