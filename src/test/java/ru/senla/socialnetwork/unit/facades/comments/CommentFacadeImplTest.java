package ru.senla.socialnetwork.unit.facades.comments;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;
import ru.senla.socialnetwork.dto.mappers.CommentMapper;
import ru.senla.socialnetwork.exceptions.comments.CommentException;
import ru.senla.socialnetwork.facades.comments.impl.CommentFacadeImpl;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.comments.CommentService;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.posts.GeneralPostService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class CommentFacadeImplTest {
  @Mock
  private CommentService commentService;
  @Mock
  private UserService userService;
  @Mock
  private FriendRequestService friendRequestService;
  @Mock
  private GeneralPostService generalPostService;
  @Mock
  private CommentMapper commentMapper;
  @Mock
  private CommunityMemberService communityMemberService;

  @InjectMocks
  private CommentFacadeImpl commentFacade;

  private User testUser;
  private User postAuthor;
  private WallPost wallPost;
  private CommunityPost communityPost;
  private Comment testComment;
  private CommentDTO testCommentDTO;
  private CreateCommentDTO createCommentDTO;
  private UpdateCommentDTO updateCommentDTO;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .profileType(ProfileType.OPEN)
        .build();

    postAuthor = User.builder()
        .id(TEST_USER_ID_2)
        .email(TEST_EMAIL_2)
        .profileType(ProfileType.CLOSED)
        .build();

    wallPost = WallPost.builder()
        .id(TEST_POST_ID)
        .wallOwner(postAuthor)
        .build();

    communityPost = CommunityPost.builder()
        .id(TEST_POST_ID)
        .community(Community.builder().id(TEST_COMMUNITY_ID).build())
        .build();

    testComment = Comment.builder()
        .id(TEST_COMMENT_ID)
        .author(testUser)
        .post(wallPost)
        .body(TEST_BODY)
        .build();

    testCommentDTO = new CommentDTO(
        TEST_COMMENT_ID,
        TEST_POST_ID,
        TEST_USER_ID_1,
        TEST_BODY);

    createCommentDTO = new CreateCommentDTO(TEST_BODY);
    updateCommentDTO = new UpdateCommentDTO("Updated body");
  }

  @Nested
  class GetAllTests {
    @Test
    void getAll_whenCalled_thenReturnAllComments() {
      List<Comment> comments = List.of(testComment);
      List<CommentDTO> expectedDTOs = List.of(testCommentDTO);

      when(commentService.getAll()).thenReturn(comments);
      when(commentMapper.toListDTO(comments)).thenReturn(expectedDTOs);

      List<CommentDTO> result = commentFacade.getAll();

      assertThat(result).isEqualTo(expectedDTOs);
      verify(commentService).getAll();
      verify(commentMapper).toListDTO(comments);
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_whenFriend_thenReturnComment() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId())).thenReturn(true);
      when(commentMapper.toDTO(testComment)).thenReturn(testCommentDTO);

      CommentDTO result = commentFacade.getById(TEST_COMMENT_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testCommentDTO);
      verify(friendRequestService).isFriends(postAuthor.getId(), testUser.getId());
    }

    @Test
    void getById_whenNoRights_thenThrowException() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId())).thenReturn(false);

      assertThatThrownBy(() -> commentFacade.getById(TEST_COMMENT_ID, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Вы не можете увидеть комментарий под этим постом");
    }
  }

  @Nested
  class GetPostCommentsTests {
    @Test
    void getPostComments_whenNoRights_thenThrowException() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(generalPostService.getPost(TEST_POST_ID)).thenReturn(wallPost);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId())).thenReturn(false);

      assertThatThrownBy(() -> commentFacade.getPostComments(TEST_POST_ID, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Вы не можете увидеть комментарии под этим постом");
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_whenNoRights_thenThrowException() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(generalPostService.getPost(TEST_POST_ID)).thenReturn(wallPost);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId())).thenReturn(false);

      assertThatThrownBy(() -> commentFacade.create(TEST_POST_ID, createCommentDTO, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Вы не можете комментировать этот пост");
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_whenAuthor_thenUpdateComment() {
      testComment.setAuthor(testUser);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(commentService.update(testComment, updateCommentDTO.body())).thenReturn(testComment);
      when(commentMapper.toDTO(testComment)).thenReturn(testCommentDTO);

      CommentDTO result = commentFacade.update(TEST_COMMENT_ID, updateCommentDTO, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testCommentDTO);
      verify(commentService).update(testComment, updateCommentDTO.body());
    }

    @Test
    void update_whenNotAuthor_thenThrowException() {
      testComment.setAuthor(postAuthor);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      assertThatThrownBy(() -> commentFacade.update(TEST_COMMENT_ID, updateCommentDTO, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Вы не можете редактировать чужие комментарии");
    }
  }

  @Nested
  class DeleteTests {

    @Test
    void delete_whenAuthor_thenDeleteComment() {
      testComment.setAuthor(testUser);
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      commentFacade.delete(TEST_COMMENT_ID, TEST_EMAIL_1);

      verify(commentService).delete(testComment);
    }

    @Test
    void delete_whenWallOwner_thenDeleteComment() {
      testComment.setAuthor(postAuthor);
      wallPost.setWallOwner(testUser);
      testComment.setPost(wallPost);

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      commentFacade.delete(TEST_COMMENT_ID, TEST_EMAIL_1);

      verify(commentService).delete(testComment);
    }

    @Test
    void delete_whenNoRights_thenThrowException() {
      testComment.setAuthor(postAuthor);
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      assertThatThrownBy(() -> commentFacade.delete(TEST_COMMENT_ID, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("У вас нет прав для удаления этого комментария");
    }
  }
}