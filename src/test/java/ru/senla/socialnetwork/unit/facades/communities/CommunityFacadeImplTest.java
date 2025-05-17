package ru.senla.socialnetwork.unit.facades.communities;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.unit.TestConstants.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.exceptions.communities.CommunityException;
import ru.senla.socialnetwork.facades.communities.impl.CommunityFacadeImpl;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.UserRole;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class CommunityFacadeImplTest {
  @Mock
  private CommunityService communityService;
  @Mock
  private CommunityMemberService communityMemberService;
  @Mock
  private UserService userService;

  @InjectMocks
  private CommunityFacadeImpl communityFacade;

  private User testUser;
  private Community testCommunity;
  private CreateCommunityDTO createCommunityDTO;
  private ChangeCommunityDTO changeCommunityDTO;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .role(UserRole.USER)
        .build();

    testCommunity = Community.builder()
        .id(TEST_COMMUNITY_ID)
        .name(TEST_COMMUNITY_NAME)
        .description("Test description")
        .created_at(TEST_DATE)
        .build();

    createCommunityDTO = new CreateCommunityDTO(
        TEST_COMMUNITY_NAME,
        "Test description");

    changeCommunityDTO = new ChangeCommunityDTO(
        TEST_COMMUNITY_ID,
        "New name",
        "New description");
  }

  @Nested
  class CreateTests {
    @Test
    void create_whenValidData_thenCreateCommunityAndSetAdmin() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(communityService.save(any(Community.class))).thenReturn(testCommunity);

      CommunityMember ownerMember = CommunityMember.builder()
          .user(testUser)
          .community(testCommunity)
          .role(MemberRole.ADMIN)
          .build();

      when(communityMemberService.joinCommunity(testCommunity, testUser))
          .thenReturn(ownerMember);

      CommunityDTO result = communityFacade.create(createCommunityDTO, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(TEST_COMMUNITY_ID);
      assertThat(result.name()).isEqualTo(TEST_COMMUNITY_NAME);

      verify(communityService).save(any(Community.class));
      verify(communityMemberService).joinCommunity(testCommunity, testUser);
      verify(communityMemberService).changeMemberRole(ownerMember, MemberRole.ADMIN);
    }
  }

  @Nested
  class DeleteTests {
    @Test
    void delete_whenAdmin_thenDeleteCommunity() {
      User adminUser = User.builder().role(UserRole.ADMIN).build();
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(adminUser);
      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);
      when(communityMemberService.getAll(TEST_COMMUNITY_ID)).thenReturn(List.of());

      communityFacade.delete(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      verify(communityService).delete(testCommunity);
    }

    @Test
    void delete_whenCommunityAdmin_thenDeleteCommunity() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);
      when(communityMemberService.isMember(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(true);
      when(communityMemberService.getAll(TEST_COMMUNITY_ID)).thenReturn(List.of());

      communityFacade.delete(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      verify(communityMemberService).checkIsAdmin(TEST_COMMUNITY_ID, TEST_EMAIL_1);
      verify(communityService).delete(testCommunity);
    }

    @Test
    void delete_whenNotAdminNorCommunityAdmin_thenThrowException() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(communityMemberService.isMember(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(false);

      assertThatThrownBy(() -> communityFacade.delete(TEST_COMMUNITY_ID, TEST_EMAIL_1))
          .isInstanceOf(CommunityException.class)
          .hasMessageContaining("Недостаточно прав");
    }
  }

  @Nested
  class GetTests {
    @Test
    void get_whenCommunityExists_thenReturnCommunityDTO() {
      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);

      CommunityDTO result = communityFacade.get(TEST_COMMUNITY_ID);

      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(TEST_COMMUNITY_ID);
      assertThat(result.name()).isEqualTo(TEST_COMMUNITY_NAME);
    }

    @Test
    void getAll_whenCommunitiesExist_thenReturnList() {
      when(communityService.getAll()).thenReturn(List.of(testCommunity));

      List<CommunityDTO> result = communityFacade.getAll();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).id()).isEqualTo(TEST_COMMUNITY_ID);
    }
  }

  @Nested
  class ChangeTests {
    @Test
    void change_whenAdmin_thenUpdateCommunity() {
      Community updatedCommunity = Community.builder()
          .id(TEST_COMMUNITY_ID)
          .name("New name")
          .description("New description")
          .build();

      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);
      when(communityService.save(any(Community.class))).thenReturn(updatedCommunity);

      CommunityDTO result = communityFacade.change(changeCommunityDTO, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result.name()).isEqualTo("New name");
      verify(communityMemberService).checkIsAdmin(TEST_COMMUNITY_ID, TEST_EMAIL_1);
    }

    @Test
    void change_whenNotAdmin_thenThrowException() {
      doThrow(new CommunityException("Недостаточно прав"))
          .when(communityMemberService).checkIsAdmin(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThatThrownBy(() -> communityFacade.change(changeCommunityDTO, TEST_EMAIL_1))
          .isInstanceOf(CommunityException.class);
    }
  }
}