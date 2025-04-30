package ru.senla.socialnetwork.controllers.communities.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityMemberController;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.facade.communities.CommunityMemberFacade;
import ru.senla.socialnetwork.model.general.MemberRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/members")
public class CommunityMemberControllerImpl implements CommunityMemberController {
  private final CommunityMemberFacade communityMemberFacade;

  @Override
  @GetMapping
  public ResponseEntity<List<CommunityMemberDTO>> getAll(@PathVariable Long communityId) {
    return ResponseEntity.ok(communityMemberFacade.getAll(communityId));
  }

  @Override
  @PostMapping("/join")
  public ResponseEntity<CommunityMemberDTO> joinCommunity(@PathVariable Long communityId,
                                            @RequestParam String userEmail) {
    return ResponseEntity.ok(communityMemberFacade.joinCommunity(communityId, userEmail));
  }

  @Override
  @PostMapping("/leave")
  public ResponseEntity<Void> leaveCommunity(@PathVariable Long communityId,
                                             @RequestParam String userEmail) {
    communityMemberFacade.leaveCommunity(communityId, userEmail);
    return ResponseEntity.ok().build();
  }

  @Override
  @PostMapping("/ban")
  @PreAuthorize("@communityServiceImpl.isOwner(#communityId, authentication.name)")
  public ResponseEntity<CommunityMemberDTO> banMember(@PathVariable Long communityId,
                                        @RequestParam String userEmail,
                                        @RequestParam String reason) {
    return ResponseEntity.ok(communityMemberFacade.banMember(communityId, userEmail, reason));
  }

  @Override
  @PostMapping("/role")
  @PreAuthorize("@communityServiceImpl.isOwner(#communityId, authentication.name)")
  public ResponseEntity<CommunityMemberDTO> changeMemberRole(@PathVariable Long communityId,
                                               @RequestParam String userEmail,
                                               @RequestParam MemberRole role) {
    return ResponseEntity.ok(communityMemberFacade.changeMemberRole(communityId, userEmail, role));
  }
}
