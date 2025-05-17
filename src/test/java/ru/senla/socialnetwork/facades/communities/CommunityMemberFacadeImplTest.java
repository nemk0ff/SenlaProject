package ru.senla.socialnetwork.facades.communities;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.TestConstants.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.exceptions.communities.CommunityMemberException;
import ru.senla.socialnetwork.facades.communities.impl.CommunityMemberFacadeImpl;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class CommunityMemberFacadeImplTest {
  @Mock
  private CommunityService communityService;
  @Mock
  private CommunityMemberService communityMemberService;
  @Mock
  private UserService userService;

  @InjectMocks
  private CommunityMemberFacadeImpl communityMemberFacade;

  private User testUser;
  private User memberUser;
  private Community testCommunity;
  private CommunityMember testMember;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .build();

    memberUser = User.builder()
        .id(TEST_USER_ID_2)
        .email(TEST_EMAIL_2)
        .build();

    testCommunity = Community.builder()
        .id(TEST_COMMUNITY_ID)
        .name(TEST_COMMUNITY_NAME)
        .build();

    testMember = CommunityMember.builder()
        .id(1L)
        .user(testUser)
        .community(testCommunity)
        .role(MemberRole.MEMBER)
        .build();
  }

  @Nested
  class GetAllTests {
    @Test
    void getAll_whenCalled_thenReturnMembersList() {
      List<CommunityMember> members = List.of(testMember);
      when(communityMemberService.getAll(TEST_COMMUNITY_ID)).thenReturn(members);

      List<CommunityMemberDTO> result = communityMemberFacade.getAll(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).email()).isEqualTo(TEST_EMAIL_1);
      verify(communityMemberService).getAll(TEST_COMMUNITY_ID);
    }
  }

  @Nested
  class JoinCommunityTests {
    @Test
    void joinCommunity_whenNewMember_thenJoinCommunity() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);
      when(communityMemberService.isMember(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(false);
      when(communityMemberService.isMemberExists(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(false);
      when(communityMemberService.joinCommunity(testCommunity, testUser)).thenReturn(testMember);

      CommunityMemberDTO result = communityMemberFacade.joinCommunity(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(TEST_EMAIL_1);
      verify(communityMemberService).joinCommunity(testCommunity, testUser);
    }

    @Test
    void joinCommunity_whenAlreadyMember_thenThrowException() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);
      when(communityMemberService.isMember(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(true);

      assertThatThrownBy(() -> communityMemberFacade.joinCommunity(TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class)
          .hasMessageContaining("уже является участником");
    }

    @Test
    void joinCommunity_whenWasMemberBefore_thenRecreate() {
      CommunityMember recreatedMember = CommunityMember.builder()
          .id(2L)
          .user(testUser)
          .community(testCommunity)
          .build();

      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);
      when(communityMemberService.isMember(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(false);
      when(communityMemberService.isMemberExists(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(true);
      when(communityMemberService.recreate(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(recreatedMember);

      CommunityMemberDTO result = communityMemberFacade.joinCommunity(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      verify(communityMemberService).recreate(TEST_COMMUNITY_ID, TEST_EMAIL_1);
    }
  }

  @Nested
  class LeaveCommunityTests {
    @Test
    void leaveCommunity_whenMember_thenLeave() {
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(testMember);
      when(communityMemberService.leaveCommunity(testMember)).thenReturn(testMember);

      CommunityMemberDTO result = communityMemberFacade.leaveCommunity(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      verify(communityMemberService).leaveCommunity(testMember);
    }
  }

  @Nested
  class BanUnbanTests {
    @Test
    void banMember_whenAdminOrModer_thenBan() {
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_2)).thenReturn(testMember);
      when(communityMemberService.banMember(testMember, TEST_BAN_REASON)).thenReturn(testMember);

      CommunityMemberDTO result = communityMemberFacade.banMember(
          TEST_COMMUNITY_ID, TEST_EMAIL_2, TEST_BAN_REASON, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      verify(communityMemberService).checkIsAdminOrModer(TEST_COMMUNITY_ID, TEST_EMAIL_1);
      verify(communityMemberService).banMember(testMember, TEST_BAN_REASON);
    }

    @Test
    void unbanMember_whenAdminOrModer_thenUnban() {
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_2)).thenReturn(testMember);
      when(communityMemberService.unbanMember(testMember)).thenReturn(testMember);

      CommunityMemberDTO result = communityMemberFacade.unbanMember(
          TEST_COMMUNITY_ID, TEST_EMAIL_2, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      verify(communityMemberService).checkIsAdminOrModer(TEST_COMMUNITY_ID, TEST_EMAIL_1);
      verify(communityMemberService).unbanMember(testMember);
    }
  }

  @Nested
  class ChangeRoleTests {
    @Test
    void changeMemberRole_whenAdmin_thenChangeRole() {
      CommunityMember adminMember = CommunityMember.builder()
          .user(memberUser)
          .role(MemberRole.ADMIN)
          .build();

      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_2)).thenReturn(testMember);
      when(communityMemberService.changeMemberRole(testMember, MemberRole.ADMIN)).thenReturn(adminMember);

      CommunityMemberDTO result = communityMemberFacade.changeMemberRole(
          TEST_COMMUNITY_ID, TEST_EMAIL_2, MemberRole.ADMIN, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result.role()).isEqualTo(MemberRole.ADMIN);
      verify(communityMemberService).checkIsAdmin(TEST_COMMUNITY_ID, TEST_EMAIL_1);
    }

    @Test
    void changeMemberRole_whenSelf_thenThrowException() {
      assertThatThrownBy(() -> communityMemberFacade.changeMemberRole(
          TEST_COMMUNITY_ID, TEST_EMAIL_1, MemberRole.ADMIN, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class)
          .hasMessageContaining("изменить свою роль");
    }
  }
}