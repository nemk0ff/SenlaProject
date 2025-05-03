package ru.senla.socialnetwork.controllers.communities;

import java.util.List;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface CommunityMemberController {
  ResponseEntity<List<CommunityMemberDTO>> getAll(Long communityId);

  ResponseEntity<CommunityMemberDTO> joinCommunity(Long communityId, String userEmail);

  ResponseEntity<String> leaveCommunity(Long communityId, String userEmail);

  ResponseEntity<CommunityMemberDTO> banMember(Long communityId, String userEmail, String reason);

  ResponseEntity<CommunityMemberDTO> unbanMember(Long communityId, String email);

  ResponseEntity<CommunityMemberDTO> changeMemberRole(Long communityId, String userEmail, MemberRole role);
}

