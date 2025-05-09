package ru.senla.socialnetwork.controllers.communities;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface CommunityMemberController {
  ResponseEntity<?> getAll(Long communityId);

  ResponseEntity<?> joinCommunity(Long communityId, String userEmail);

  ResponseEntity<?> leaveCommunity(Long communityId, String userEmail);

  ResponseEntity<?> banMember(Long communityId, String userEmail, String reason);

  ResponseEntity<?> unbanMember(Long communityId, String email);

  ResponseEntity<?> changeMemberRole(Long communityId, String userEmail, MemberRole role);
}

