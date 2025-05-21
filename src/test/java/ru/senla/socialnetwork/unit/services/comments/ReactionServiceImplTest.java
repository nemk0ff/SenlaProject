package ru.senla.socialnetwork.unit.services.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.socialnetwork.dao.comments.ReactionDao;
import ru.senla.socialnetwork.exceptions.comments.ReactionException;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.comment.Reaction;
import ru.senla.socialnetwork.model.comment.ReactionType;
import ru.senla.socialnetwork.model.users.User;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.services.comments.impl.ReactionServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReactionServiceImplTest {
  @Mock
  private ReactionDao reactionDao;

  @InjectMocks
  private ReactionServiceImpl reactionService;

  private Reaction testReaction;
  private User testUser;
  private Comment testComment;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .name(TEST_NAME)
        .build();

    testComment = Comment.builder()
        .id(1L)
        .body(TEST_BODY)
        .author(testUser)
        .build();

    testReaction = Reaction.builder()
        .id(1L)
        .type(ReactionType.LIKE)
        .owner(testUser)
        .comment(testComment)
        .createdAt(ZonedDateTime.now())
        .build();
  }

  @Nested
  class GetAllTests {
    @Test
    void getAll_whenReactionsExist_thenReturnReactions() {
      List<Reaction> expected = List.of(testReaction);
      when(reactionDao.findAll()).thenReturn(expected);

      List<Reaction> result = reactionService.getAll();

      assertThat(result).isEqualTo(expected);
      verify(reactionDao).findAll();
    }
  }

  @Nested
  class GetAllByCommentTests {
    @Test
    void getAllByComment_whenReactionsExist_thenReturnReactions() {
      List<Reaction> expected = List.of(testReaction);
      when(reactionDao.findAllByComment(TEST_COMMENT_ID)).thenReturn(expected);

      List<Reaction> result = reactionService.getAllByComment(TEST_COMMENT_ID);

      assertThat(result).isEqualTo(expected);
      verify(reactionDao).findAllByComment(TEST_COMMENT_ID);
    }
  }

  @Nested
  class GetTests {
    @Test
    void get_whenReactionExists_thenReturnReaction() {
      when(reactionDao.find(TEST_REACTION_ID)).thenReturn(Optional.of(testReaction));

      Reaction result = reactionService.get(TEST_REACTION_ID);

      assertThat(result).isEqualTo(testReaction);
      verify(reactionDao).find(TEST_REACTION_ID);
    }

    @Test
    void get_whenReactionNotExists_thenThrowException() {
      when(reactionDao.find(TEST_REACTION_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> reactionService.get(TEST_REACTION_ID))
          .isInstanceOf(ReactionException.class)
          .hasMessageContaining("Реакция не найдена");
    }
  }

  @Nested
  class SaveTests {
    @Test
    void save_whenNewReaction_thenSaveAndReturn() {
      when(reactionDao.findByUserIdAndCommentId(anyLong(), anyLong())).thenReturn(Optional.empty());
      when(reactionDao.saveOrUpdate(any(Reaction.class))).thenReturn(testReaction);

      Reaction result = reactionService.save(testReaction);

      assertThat(result).isEqualTo(testReaction);
      verify(reactionDao).saveOrUpdate(testReaction);
    }

    @Test
    void save_whenExistingReactionWithDifferentType_thenUpdateAndReturn() {
      ZonedDateTime oldDate = ZonedDateTime.now().minusDays(1);
      Reaction existingReaction = Reaction.builder()
          .id(2L)
          .type(ReactionType.DISLIKE)
          .owner(testUser)
          .comment(testComment)
          .createdAt(oldDate)
          .build();

      when(reactionDao.findByUserIdAndCommentId(testComment.getId(), testUser.getId()))
          .thenReturn(Optional.of(existingReaction));
      when(reactionDao.saveOrUpdate(any(Reaction.class))).thenAnswer(invocation -> {
        Reaction r = invocation.getArgument(0);
        r.setType(testReaction.getType());
        r.setCreatedAt(ZonedDateTime.now());
        return r;
      });

      Reaction result = reactionService.save(testReaction);

      assertThat(result.getType()).isEqualTo(testReaction.getType());
      assertThat(result.getCreatedAt()).isAfterOrEqualTo(oldDate);
      verify(reactionDao).saveOrUpdate(existingReaction);
    }

    @Test
    void save_whenExistingReactionWithSameType_thenReturnExisting() {
      Reaction existingReaction = Reaction.builder()
          .id(2L)
          .type(ReactionType.LIKE)
          .owner(testUser)
          .comment(testComment)
          .createdAt(ZonedDateTime.now().minusDays(1))
          .build();

      when(reactionDao.findByUserIdAndCommentId(testComment.getId(), testUser.getId()))
          .thenReturn(Optional.of(existingReaction));

      Reaction result = reactionService.save(testReaction);

      assertThat(result).isEqualTo(existingReaction);
      verify(reactionDao, never()).saveOrUpdate(any(Reaction.class));
    }
  }
}
