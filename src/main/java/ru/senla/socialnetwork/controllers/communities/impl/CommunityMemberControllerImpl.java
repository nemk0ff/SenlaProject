package ru.senla.socialnetwork.controllers.communities.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityMemberController;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.facades.communities.CommunityMemberFacade;
import ru.senla.socialnetwork.model.MemberRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/members")
public class CommunityMemberControllerImpl implements CommunityMemberController {
  private final CommunityMemberFacade communityMemberFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> getAll(
      @PathVariable Long communityId,
      Authentication auth) {
    log.info("Запрос списка участников сообщества {} пользователем {}...", communityId,
        auth.getName());
    List<CommunityMemberDTO> members = communityMemberFacade.getAll(communityId, auth.getName());
    log.info("Возвращено {} участников сообщества {}.", members.size(), communityId);
    return ResponseEntity.ok(members);
  }

  @Override
  @PostMapping
  public ResponseEntity<?> joinCommunity(
      @PathVariable Long communityId,
      Authentication auth) {
    log.info("Пользователь {} вступает в сообщество {}...", auth.getName(), communityId);
    CommunityMemberDTO member = communityMemberFacade.joinCommunity(communityId, auth.getName());
    log.info("Пользователь {} успешно вступил в сообщество {}.", auth.getName(), communityId);
    return ResponseEntity.ok(member);
  }

  @Override
  @DeleteMapping
  public ResponseEntity<?> leaveCommunity(
      @PathVariable Long communityId,
      Authentication auth) {
    log.info("Пользователь {} покидает сообщество {}...", auth.getName(), communityId);
    CommunityMemberDTO member = communityMemberFacade.leaveCommunity(communityId, auth.getName());
    log.info("Пользователь {} вышел из сообщества {}", auth.getName(), communityId);
    return ResponseEntity.ok(member);
  }

  @Override
  @PostMapping("/ban")
  public ResponseEntity<?> banMember(
      @PathVariable Long communityId,
      @RequestParam String email,
      @RequestParam String reason,
      Authentication auth) {
    log.info("Пользователь {} блокирует {} в сообществе {} по причине '{}'...",
        auth.getName(), email, communityId, reason);
    CommunityMemberDTO bannedMember = communityMemberFacade.banMember(
        communityId, email, reason, auth.getName());
    log.info("Пользователь {} заблокирован в сообществе {} пользователем {}.",
        email, communityId, auth.getName());
    return ResponseEntity.ok(bannedMember);
  }

  @Override
  @PostMapping("/unban")
  public ResponseEntity<?> unbanMember(
      @PathVariable Long communityId,
      @RequestParam String email,
      Authentication auth) {
    log.info("Пользователь {} разблокирует {} в сообществе {}...", auth.getName(), email,
        communityId);
    CommunityMemberDTO unbannedMember = communityMemberFacade.unbanMember(communityId, email, auth.getName());
    log.info("Пользователь {} разблокирован в сообществе {}.", email, communityId);
    return ResponseEntity.ok(unbannedMember);
  }

  @Override
  @PostMapping("/role")
  public ResponseEntity<?> changeMemberRole(
      @PathVariable Long communityId,
      @RequestParam String email,
      @RequestParam MemberRole role,
      Authentication auth) {
    log.info("Изменение роли пользователя {} в сообществе {} на {} пользователем {}...",
        email, communityId, role, auth.getName());
    CommunityMemberDTO updatedMember = communityMemberFacade.changeMemberRole(
        communityId, email, role, auth.getName());
    log.info("Роль пользователя {} изменена на {} в сообществе {}.",
        email, role, communityId);
    return ResponseEntity.ok(updatedMember);
  }
}
