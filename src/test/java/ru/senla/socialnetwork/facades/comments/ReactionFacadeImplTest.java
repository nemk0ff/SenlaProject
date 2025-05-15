package ru.senla.socialnetwork.facades.comments;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.exceptions.comments.CommentException;
import ru.senla.socialnetwork.exceptions.comments.ReactionException;
import ru.senla.socialnetwork.facades.comments.impl.ReactionFacadeImpl;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.comment.Reaction;
import ru.senla.socialnetwork.model.comment.ReactionType;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.comments.CommentService;
import ru.senla.socialnetwork.services.comments.ReactionService;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class ReactionFacadeImplTest {
  @Mock
  private ReactionService reactionService;
  @Mock
  private FriendRequestService friendRequestService;
  @Mock
  private UserService userService;
  @Mock
  private CommentService commentService;

  @InjectMocks
  private ReactionFacadeImpl reactionFacade;

  private User testUser;
  private User postAuthor;
  private WallPost wallPost;
  private Comment testComment;
  private Reaction testReaction;
  private ReactionDTO testReactionDTO;

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

    testComment = Comment.builder()
        .id(TEST_COMMENT_ID)
        .post(wallPost)
        .build();

    testReaction = Reaction.builder()
        .id(1L)
        .owner(testUser)
        .comment(testComment)
        .type(ReactionType.LIKE)
        .createdAt(TEST_DATE)
        .build();

    testReactionDTO = new ReactionDTO(
        1L,
        TEST_EMAIL_1,
        TEST_COMMENT_ID,
        ReactionType.LIKE);
  }

  @Nested
  class GetAllTests {
    @Test
    void getAll_whenCalled_thenReturnAllReactions() {
      List<Reaction> reactions = List.of(testReaction);
      List<ReactionDTO> expectedDTOs = List.of(testReactionDTO);

      when(reactionService.getAll()).thenReturn(reactions);

      List<ReactionDTO> result = reactionFacade.getAll();

      assertThat(result).isEqualTo(expectedDTOs);
      verify(reactionService).getAll();
    }
  }

  @Nested
  class GetByCommentTests {
    @Test
    void getByComment_whenAdmin_thenReturnReactions() {
      List<Reaction> reactions = List.of(testReaction);
      List<ReactionDTO> expectedDTOs = List.of(testReactionDTO);

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(true);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(reactionService.getAllByComment(TEST_COMMENT_ID)).thenReturn(reactions);

      List<ReactionDTO> result = reactionFacade.getByComment(TEST_COMMENT_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(expectedDTOs);
      verify(userService).isAdmin(TEST_EMAIL_1);
      verify(reactionService).getAllByComment(TEST_COMMENT_ID);
    }

    @Test
    void getByComment_whenWallOwner_thenReturnReactions() {
      List<Reaction> reactions = List.of(testReaction);
      List<ReactionDTO> expectedDTOs = List.of(testReactionDTO);

      when(userService.isAdmin(TEST_EMAIL_2)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(postAuthor);
      when(reactionService.getAllByComment(TEST_COMMENT_ID)).thenReturn(reactions);

      List<ReactionDTO> result = reactionFacade.getByComment(TEST_COMMENT_ID, TEST_EMAIL_2);

      assertThat(result).isEqualTo(expectedDTOs);
      verify(userService).getUserByEmail(TEST_EMAIL_2);
    }

    @Test
    void getByComment_whenFriend_thenReturnReactions() {
      List<Reaction> reactions = List.of(testReaction);
      List<ReactionDTO> expectedDTOs = List.of(testReactionDTO);

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId())).thenReturn(true);
      when(reactionService.getAllByComment(TEST_COMMENT_ID)).thenReturn(reactions);

      List<ReactionDTO> result = reactionFacade.getByComment(TEST_COMMENT_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(expectedDTOs);
      verify(friendRequestService).isFriends(postAuthor.getId(), testUser.getId());
    }

    @Test
    void getByComment_whenNoAccess_thenThrowException() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId())).thenReturn(false);

      assertThatThrownBy(() -> reactionFacade.getByComment(TEST_COMMENT_ID, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("У вас нет доступа");
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_whenAdmin_thenReturnReaction() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(true);
      when(reactionService.get(1L)).thenReturn(testReaction);

      ReactionDTO result = reactionFacade.getById(1L, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testReactionDTO);
      verify(userService).isAdmin(TEST_EMAIL_1);
    }

    @Test
    void getById_whenWallOwner_thenReturnReaction() {
      wallPost.setWallOwner(postAuthor);
      testComment.setPost(wallPost);
      testReaction.setComment(testComment);
      testReaction.setOwner(postAuthor);

      when(userService.isAdmin(TEST_EMAIL_2)).thenReturn(false);
      when(reactionService.get(1L)).thenReturn(testReaction);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(postAuthor);

      ReactionDTO result = reactionFacade.getById(1L, TEST_EMAIL_2);

      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(TEST_EMAIL_2);
      assertThat(result.commentId()).isEqualTo(TEST_COMMENT_ID);
      assertThat(result.type()).isEqualTo(ReactionType.LIKE);

      verify(userService).getUserByEmail(TEST_EMAIL_2);
    }

    @Test
    void getById_whenFriend_thenReturnReaction() {
      wallPost.setWallOwner(postAuthor);
      postAuthor.setProfileType(ProfileType.CLOSED);
      testComment.setPost(wallPost);
      testReaction.setComment(testComment);
      testReaction.setOwner(testUser);

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(reactionService.get(1L)).thenReturn(testReaction);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId()))
          .thenReturn(true);

      ReactionDTO result = reactionFacade.getById(1L, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(TEST_EMAIL_1);
      assertThat(result.commentId()).isEqualTo(TEST_COMMENT_ID);
      assertThat(result.type()).isEqualTo(ReactionType.LIKE);

      verify(friendRequestService).isFriends(postAuthor.getId(), testUser.getId());
    }

    @Test
    void getById_whenNoAccess_thenThrowException() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(reactionService.get(1L)).thenReturn(testReaction);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId())).thenReturn(false);

      assertThatThrownBy(() -> reactionFacade.getById(1L, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Вы не можете увидеть комментарий");
    }
  }

  @Nested
  class SetReactionTests {
    @Test
    void setReaction_whenAdmin_thenSetReaction() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(true);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      Reaction savedReaction = Reaction.builder()
          .id(TEST_REACTION_ID)
          .owner(testUser)
          .comment(testComment)
          .type(ReactionType.LIKE)
          .build();

      when(reactionService.save(any(Reaction.class))).thenReturn(savedReaction);

      ReactionDTO result = reactionFacade.setReaction(
          TEST_COMMENT_ID, ReactionType.LIKE, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result).isEqualTo(testReactionDTO);
      verify(reactionService).save(any(Reaction.class));
    }

    @Test
    void setReaction_whenWallOwner_thenSetReaction() {
      wallPost.setWallOwner(postAuthor);
      postAuthor.setProfileType(ProfileType.OPEN);

      when(userService.isAdmin(TEST_EMAIL_2)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(postAuthor);

      Reaction savedReaction = Reaction.builder()
          .owner(postAuthor)
          .comment(testComment)
          .type(ReactionType.LIKE)
          .build();

      when(reactionService.save(any(Reaction.class))).thenReturn(savedReaction);

      ReactionDTO result = reactionFacade.setReaction(
          TEST_COMMENT_ID, ReactionType.LIKE, TEST_EMAIL_2);

      assertThat(result).isNotNull();
      assertThat(result.email()).isEqualTo(TEST_EMAIL_2);
      verify(reactionService).save(any(Reaction.class));
    }

    @Test
    void setReaction_whenNoAccess_thenThrowException() {
      wallPost.setWallOwner(postAuthor);
      postAuthor.setProfileType(ProfileType.CLOSED);
      testComment.setPost(wallPost);

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(commentService.getById(TEST_COMMENT_ID)).thenReturn(testComment);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      when(friendRequestService.isFriends(postAuthor.getId(), testUser.getId()))
          .thenReturn(false);

      assertThatThrownBy(() -> reactionFacade.setReaction(
          TEST_COMMENT_ID, ReactionType.LIKE, TEST_EMAIL_1))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Вы не можете реагировать на этот комментарий");
    }
  }

  @Nested
  class RemoveReactionTests {
    @Test
    void removeReaction_whenOwner_thenRemoveReaction() {
      when(reactionService.get(1L)).thenReturn(testReaction);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      reactionFacade.removeReaction(1L, TEST_EMAIL_1);

      verify(reactionService).delete(testReaction);
    }

    @Test
    void removeReaction_whenNotOwner_thenThrowException() {
      when(reactionService.get(1L)).thenReturn(testReaction);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(postAuthor);

      assertThatThrownBy(() -> reactionFacade.removeReaction(1L, TEST_EMAIL_2))
          .isInstanceOf(ReactionException.class)
          .hasMessageContaining("У вас нет прав для удаления этой реакции");
    }
  }
}