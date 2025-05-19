package ru.senla.socialnetwork.unit.facades.wallposts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.unit.TestConstants.*;

import org.junit.jupiter.api.Nested;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;
import ru.senla.socialnetwork.exceptions.users.WallPostException;
import ru.senla.socialnetwork.facades.wallposts.impl.WallPostFacadeImpl;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.UserRole;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.posts.WallPostService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class WallPostFacadeImplTest {
  @Mock
  private UserService userService;
  @Mock
  private FriendRequestService friendRequestService;
  @Mock
  private WallPostService wallPostService;

  @InjectMocks
  private WallPostFacadeImpl wallPostFacade;

  private User testUser;
  private User friendUser;
  private User notFriendUser;
  private WallPost testPost;
  private WallPostRequestDTO postRequestDTO;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .profileType(ProfileType.CLOSED)
        .build();

    friendUser = User.builder()
        .id(TEST_USER_ID_2)
        .email(TEST_EMAIL_2)
        .build();

    notFriendUser = User.builder()
        .id(3L)
        .email("not_friend@senla.ru")
        .build();

    testPost = WallPost.builder()
        .id(TEST_POST_ID)
        .wallOwner(testUser)
        .body(TEST_BODY)
        .createdAt(TEST_DATE)
        .build();

    postRequestDTO = new WallPostRequestDTO(
        TEST_BODY,
        TEST_MOOD,
        TEST_LOCATION);
  }

  @Nested
  class GetByUserTests {
    @Test
    void getByUser_whenAdmin_thenReturnPosts() {
      User admin = User.builder()
          .email(TEST_EMAIL_2)
          .role(UserRole.ADMIN)
          .build();

      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(admin);
      when(userService.isAdmin(TEST_EMAIL_2)).thenReturn(true);
      when(wallPostService.getByUser(testUser.getId())).thenReturn(List.of(testPost));

      List<WallPostResponseDTO> result = wallPostFacade.getByUser(TEST_EMAIL_1, TEST_EMAIL_2);

      assertThat(result).hasSize(1);
    }

    @Test
    void getByUser_whenOwner_thenReturnPosts() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(wallPostService.getByUser(testUser.getId())).thenReturn(List.of(testPost));

      List<WallPostResponseDTO> result = wallPostFacade.getByUser(TEST_EMAIL_1, TEST_EMAIL_1);

      assertThat(result).hasSize(1);
    }

    @Test
    void getByUser_whenFriendAndClosedProfile_thenReturnPosts() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(friendUser);
      when(friendRequestService.isFriends(testUser.getId(), friendUser.getId())).thenReturn(true);
      when(wallPostService.getByUser(testUser.getId())).thenReturn(List.of(testPost));

      List<WallPostResponseDTO> result = wallPostFacade.getByUser(TEST_EMAIL_1, TEST_EMAIL_2);

      assertThat(result).hasSize(1);
    }

    @Test
    void getByUser_whenOpenProfile_thenReturnPosts() {
      testUser.setProfileType(ProfileType.OPEN);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail("not_friend@senla.ru")).thenReturn(notFriendUser);
      when(wallPostService.getByUser(testUser.getId())).thenReturn(List.of(testPost));

      List<WallPostResponseDTO> result = wallPostFacade.getByUser(TEST_EMAIL_1, "not_friend@senla.ru");

      assertThat(result).hasSize(1);
    }

    @Test
    void getByUser_whenNoAccess_thenThrowException() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail("not_friend@senla.ru")).thenReturn(notFriendUser);
      when(friendRequestService.isFriends(testUser.getId(), notFriendUser.getId())).thenReturn(false);

      assertThatThrownBy(() -> wallPostFacade.getByUser(TEST_EMAIL_1, "not_friend@senla.ru"))
          .isInstanceOf(WallPostException.class)
          .hasMessageContaining("нет доступа");
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_whenAdmin_thenReturnPost() {
      User admin = User.builder()
          .email(TEST_EMAIL_2)
          .role(UserRole.ADMIN)
          .build();

      when(wallPostService.get(TEST_POST_ID)).thenReturn(testPost);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(admin);
      when(userService.isAdmin(TEST_EMAIL_2)).thenReturn(true);

      WallPostResponseDTO result = wallPostFacade.getById(TEST_POST_ID, TEST_EMAIL_2);

      assertThat(result).isNotNull();
      assertThat(result.body()).isEqualTo(TEST_BODY);
    }

    @Test
    void getById_whenNoAccess_thenThrowException() {
      when(wallPostService.get(TEST_POST_ID)).thenReturn(testPost);
      when(userService.getUserByEmail("not_friend@senla.ru")).thenReturn(notFriendUser);
      when(friendRequestService.isFriends(testUser.getId(), notFriendUser.getId())).thenReturn(false);

      assertThatThrownBy(() -> wallPostFacade.getById(TEST_POST_ID, "not_friend@senla.ru"))
          .isInstanceOf(WallPostException.class);
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_whenValid_thenReturnCreatedPost() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(wallPostService.create(postRequestDTO, testUser)).thenReturn(testPost);

      WallPostResponseDTO result = wallPostFacade.create(postRequestDTO, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result.body()).isEqualTo(TEST_BODY);
      verify(wallPostService).create(postRequestDTO, testUser);
    }
  }

  @Nested
  class DeleteTests {
    @Test
    void delete_whenOwner_thenDeletePost() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(wallPostService.get(TEST_POST_ID)).thenReturn(testPost);

      wallPostFacade.delete(TEST_POST_ID, TEST_EMAIL_1);

      verify(wallPostService).delete(testPost);
    }

    @Test
    void delete_whenAdmin_thenDeletePost() {
      User admin = User.builder().role(UserRole.ADMIN).build();
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(admin);
      when(userService.isAdmin(TEST_EMAIL_2)).thenReturn(true);
      when(wallPostService.get(TEST_POST_ID)).thenReturn(testPost);

      wallPostFacade.delete(TEST_POST_ID, TEST_EMAIL_2);

      verify(wallPostService).delete(testPost);
    }

    @Test
    void delete_whenNotOwnerNorAdmin_thenThrowException() {
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(friendUser);
      when(userService.isAdmin(TEST_EMAIL_2)).thenReturn(false);
      when(wallPostService.get(TEST_POST_ID)).thenReturn(testPost);

      assertThatThrownBy(() -> wallPostFacade.delete(TEST_POST_ID, TEST_EMAIL_2))
          .isInstanceOf(WallPostException.class)
          .hasMessageContaining("не хватает прав");
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_whenOwner_thenUpdatePost() {
      WallPost updatedPost = WallPost.builder()
          .id(TEST_POST_ID)
          .body("Updated body")
          .build();

      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(wallPostService.get(TEST_POST_ID)).thenReturn(testPost);
      when(wallPostService.update(testPost, postRequestDTO)).thenReturn(updatedPost);

      WallPostResponseDTO result = wallPostFacade.update(TEST_POST_ID, postRequestDTO, TEST_EMAIL_1);

      assertThat(result.body()).isEqualTo("Updated body");
      verify(wallPostService).update(testPost, postRequestDTO);
    }

    @Test
    void update_whenNotOwner_thenThrowException() {
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(friendUser);
      when(wallPostService.get(TEST_POST_ID)).thenReturn(testPost);

      assertThatThrownBy(() -> wallPostFacade.update(TEST_POST_ID, postRequestDTO, TEST_EMAIL_2))
          .isInstanceOf(WallPostException.class);
    }
  }
}