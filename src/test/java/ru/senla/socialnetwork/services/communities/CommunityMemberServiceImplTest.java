package ru.senla.socialnetwork.services.communities;


import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import static ru.senla.socialnetwork.TestConstants.TEST_BAN_REASON;
import static ru.senla.socialnetwork.TestConstants.TEST_COMMUNITY_ID;
import static ru.senla.socialnetwork.TestConstants.TEST_COMMUNITY_NAME;
import static ru.senla.socialnetwork.TestConstants.TEST_EMAIL_1;
import static ru.senla.socialnetwork.TestConstants.TEST_USER_ID;
import ru.senla.socialnetwork.dao.communities.CommunityMemberDao;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.exceptions.communities.CommunityMemberException;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.communities.impl.CommunityMemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommunityMemberServiceImplTest {
  @Mock
  private CommunityMemberDao communityMemberDao;

  @InjectMocks
  private CommunityMemberServiceImpl communityMemberService;

  private Community testCommunity;
  private User testUser;
  private CommunityMember testMember;

  @BeforeEach
  void setUp() {
    testCommunity = Community.builder()
        .id(TEST_COMMUNITY_ID)
        .name(TEST_COMMUNITY_NAME)
        .build();

    testUser = User.builder()
        .id(TEST_USER_ID)
        .email(TEST_EMAIL_1)
        .build();

    testMember = CommunityMember.builder()
        .id(1L)
        .community(testCommunity)
        .user(testUser)
        .role(MemberRole.MEMBER)
        .joinDate(ZonedDateTime.now().minusDays(1))
        .isBanned(false)
        .build();
  }

  @Nested
  class GetMemberTests {
    @Test
    void get_whenMemberExists_thenReturnMember() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      CommunityMember result = communityMemberService.get(
          TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMember);
    }

    @Test
    void get_whenMemberNotExists_thenThrowException() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> communityMemberService.get(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Участник сообщества не найден");
    }

    @Test
    void get_whenMemberLeft_thenThrowException() {
      testMember.setLeaveDate(ZonedDateTime.now());
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      assertThatThrownBy(() -> communityMemberService.get(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("не является участником");
    }
  }

  @Nested
  class GetAllTests {
    @Test
    void getAllByCommunity_thenReturnMembers() {
      List<CommunityMember> expected = List.of(testMember);
      when(communityMemberDao.findAllByCommunityId(TEST_COMMUNITY_ID))
          .thenReturn(expected);

      List<CommunityMember> result = communityMemberService.getAll(TEST_COMMUNITY_ID);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllByUser_thenReturnMembers() {
      List<CommunityMember> expected = List.of(testMember);
      when(communityMemberDao.findAllByUserId(TEST_USER_ID))
          .thenReturn(expected);

      List<CommunityMember> result = communityMemberService.getAllByUser(TEST_USER_ID);

      assertThat(result).isEqualTo(expected);
    }
  }

  @Nested
  class JoinCommunityTests {
    @Test
    void joinCommunity_whenValid_thenReturnNewMember() {
      when(communityMemberDao.saveOrUpdate(any(CommunityMember.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      CommunityMember result = communityMemberService.joinCommunity(
          testCommunity, testUser);

      assertThat(result)
          .isNotNull()
          .hasFieldOrPropertyWithValue("community", testCommunity)
          .hasFieldOrPropertyWithValue("user", testUser)
          .hasFieldOrPropertyWithValue("role", MemberRole.MEMBER)
          .hasFieldOrPropertyWithValue("isBanned", false);

      assertThat(result.getJoinDate()).isBeforeOrEqualTo(ZonedDateTime.now());
    }
  }

  @Nested
  class LeaveCommunityTests {
    @Test
    void leaveCommunity_whenActiveMember_thenSetLeaveDate() {
      when(communityMemberDao.saveOrUpdate(any(CommunityMember.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      CommunityMember result = communityMemberService.leaveCommunity(testMember);

      assertThat(result.getLeaveDate()).isBeforeOrEqualTo(ZonedDateTime.now());
    }
  }

  @Nested
  class RecreateTests {
    @Test
    void recreate_whenMemberExists_thenUpdateJoinDate() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));
      when(communityMemberDao.saveOrUpdate(any(CommunityMember.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      ZonedDateTime beforeCall = ZonedDateTime.now();
      CommunityMember result = communityMemberService.recreate(
          TEST_COMMUNITY_ID, TEST_EMAIL_1);
      ZonedDateTime afterCall = ZonedDateTime.now();

      assertThat(result.getJoinDate())
          .isAfterOrEqualTo(beforeCall)
          .isBeforeOrEqualTo(afterCall);
    }

    @Test
    void recreate_whenMemberNotExists_thenThrowException() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> communityMemberService.recreate(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class)
          .hasMessageContaining("Ошибка при повторном вступлении");
    }
  }

  @Nested
  class BanTests {
    @Test
    void banMember_thenSetBannedTrueAndReason() {
      when(communityMemberDao.saveOrUpdate(any(CommunityMember.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      CommunityMember result = communityMemberService.banMember(
          testMember, TEST_BAN_REASON);

      assertThat(result)
          .hasFieldOrPropertyWithValue("isBanned", true)
          .hasFieldOrPropertyWithValue("bannedReason", TEST_BAN_REASON);
    }

    @Test
    void unbanMember_thenSetBannedFalseAndNullReason() {
      testMember.setIsBanned(true);
      testMember.setBannedReason(TEST_BAN_REASON);

      when(communityMemberDao.saveOrUpdate(any(CommunityMember.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      CommunityMember result = communityMemberService.unbanMember(testMember);

      assertThat(result)
          .hasFieldOrPropertyWithValue("isBanned", false)
          .hasFieldOrPropertyWithValue("bannedReason", null);
    }
  }

  @Nested
  class ChangeRoleTests {
    @Test
    void changeMemberRole_thenUpdateRole() {
      when(communityMemberDao.saveOrUpdate(any(CommunityMember.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      CommunityMember result = communityMemberService.changeMemberRole(
          testMember, MemberRole.MODERATOR);

      assertThat(result.getRole()).isEqualTo(MemberRole.MODERATOR);
    }
  }

  @Nested
  class CheckTests {
    @Test
    void checkIsBanned_whenBanned_thenThrowException() {
      testMember.setIsBanned(true);
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      assertThatThrownBy(() -> communityMemberService.checkIsBanned(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class)
          .hasMessageContaining("Вы забанены");
    }

    @Test
    void checkIsAdminOrModer_whenMember_thenThrowException() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      assertThatThrownBy(() -> communityMemberService.checkIsAdminOrModer(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class)
          .hasMessageContaining("недостаточно прав");
    }

    @Test
    void checkIsAdmin_whenNotAdmin_thenThrowException() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      assertThatThrownBy(() -> communityMemberService.checkIsAdmin(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class)
          .hasMessageContaining("недостаточно прав");
    }
  }

  @Nested
  class OtherMethodsTests {
    @Test
    void isMember_whenActive_thenReturnTrue() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      boolean result = communityMemberService.isMember(
          TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThat(result).isTrue();
    }

    @Test
    void isMemberExists_whenExists_thenReturnTrue() {
      when(communityMemberDao.findByCommunityIdAndUserEmail(
          TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      boolean result = communityMemberService.isMemberExists(
          TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThat(result).isTrue();
    }

    @Test
    void delete_whenCalled_thenCallDaoDelete() {
      communityMemberService.delete(testMember);
      verify(communityMemberDao).delete(testMember);
    }
  }
}
