package ru.senla.socialnetwork.controllers.communities;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.communitites.BanCommunityMemberDTO;
import ru.senla.socialnetwork.model.MemberRole;

public interface CommunityMemberController {
  ResponseEntity<?> getAll(Long communityId, Authentication auth);

  ResponseEntity<?> joinCommunity(Long communityId, Authentication auth);

  ResponseEntity<?> leaveCommunity(Long communityId, Authentication auth);

  ResponseEntity<?> banMember(Long communityId, @Valid BanCommunityMemberDTO dto, Authentication auth);

  ResponseEntity<?> unbanMember(Long communityId, String email, Authentication auth);

  ResponseEntity<?> changeMemberRole(Long communityId, String userEmail, MemberRole role, Authentication auth);
}

