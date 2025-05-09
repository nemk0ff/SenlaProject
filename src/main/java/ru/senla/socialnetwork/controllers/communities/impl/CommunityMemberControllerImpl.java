package ru.senla.socialnetwork.controllers.communities.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityMemberController;
import ru.senla.socialnetwork.facades.communities.CommunityMemberFacade;
import ru.senla.socialnetwork.model.general.MemberRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/members")
public class CommunityMemberControllerImpl implements CommunityMemberController {
  private final CommunityMemberFacade communityMemberFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> getAll(@PathVariable Long communityId) {
    return ResponseEntity.ok(communityMemberFacade.getAll(communityId));
  }

  @Override
  @PostMapping
  public ResponseEntity<?> joinCommunity(
      @PathVariable Long communityId,
      @RequestParam String email) {
    return ResponseEntity.ok(communityMemberFacade.joinCommunity(communityId, email));
  }

  @Override
  @DeleteMapping
  public ResponseEntity<?> leaveCommunity(
      @PathVariable Long communityId,
      @RequestParam String email) {
    communityMemberFacade.leaveCommunity(communityId, email);
    return ResponseEntity.ok("Пользователь " + email + " вышел из сообщества " + communityId);
  }

  @Override
  @PostMapping("/ban")
  @PreAuthorize("@communityMemberFacadeImpl.isAdmin(#communityId, authentication.name) OR " +
      "@communityMemberFacadeImpl.isModerator(#communityId, authentication.name)")
  public ResponseEntity<?> banMember(
      @PathVariable Long communityId,
      @RequestParam String email,
      @RequestParam String reason) {
    return ResponseEntity.ok(communityMemberFacade.banMember(communityId, email, reason));
  }

  @Override
  @PostMapping("/unban")
  @PreAuthorize("@communityMemberFacadeImpl.isAdmin(#communityId, authentication.name) OR " +
      "@communityMemberFacadeImpl.isModerator(#communityId, authentication.name)")
  public ResponseEntity<?> unbanMember(
      @PathVariable Long communityId,
      @RequestParam String email) {
    return ResponseEntity.ok(communityMemberFacade.unbanMember(communityId, email));
  }

  @Override
  @PostMapping("/role")
  @PreAuthorize("@communityMemberFacadeImpl.isAdmin(#communityId, authentication.name) " +
      "AND !authentication.name.equals(#email)")
  public ResponseEntity<?> changeMemberRole(
      @PathVariable Long communityId,
      @RequestParam String email,
      @RequestParam MemberRole role) {
    return ResponseEntity.ok(communityMemberFacade.changeMemberRole(communityId, email, role));
  }
}
