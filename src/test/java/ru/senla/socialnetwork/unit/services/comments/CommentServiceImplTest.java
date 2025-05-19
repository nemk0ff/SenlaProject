package ru.senla.socialnetwork.unit.services.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.socialnetwork.dao.comments.CommentDao;
import ru.senla.socialnetwork.exceptions.comments.CommentException;
import ru.senla.socialnetwork.model.Post;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.services.comments.impl.CommentServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
  @Mock
  private CommentDao commentDao;

  @InjectMocks
  private CommentServiceImpl commentService;

  private Comment testComment;
  private Post testPost;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .name(TEST_NAME)
        .build();

    testPost = WallPost.builder()
        .id(TEST_POST_ID)
        .body(TEST_BODY)
        .wallOwner(testUser)
        .build();

    testComment = Comment.builder()
        .id(TEST_COMMENT_ID)
        .post(testPost)
        .author(testUser)
        .body(TEST_BODY)
        .build();
  }

  @Nested
  class GetAllTests {
    @Test
    void getAll_whenCommentsExist_thenReturnComments() {
      List<Comment> expectedComments = List.of(testComment);
      when(commentDao.findAll()).thenReturn(expectedComments);

      List<Comment> result = commentService.getAll();

      assertThat(result).isEqualTo(expectedComments);
      verify(commentDao).findAll();
    }

    @Test
    void getAll_whenNoComments_thenThrowException() {
      when(commentDao.findAll()).thenReturn(Collections.emptyList());

      assertThatThrownBy(() -> commentService.getAll())
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Не найдено комментариев");
    }
  }

  @Nested
  class GetAllByPostTests {
    @Test
    void getAllByPost_whenCommentsExist_thenReturnComments() {
      List<Comment> expectedComments = List.of(testComment);
      when(commentDao.findAllByPostId(TEST_POST_ID)).thenReturn(expectedComments);

      List<Comment> result = commentService.getAllByPost(TEST_POST_ID);

      assertThat(result).isEqualTo(expectedComments);
      verify(commentDao).findAllByPostId(TEST_POST_ID);
    }

    @Test
    void getAllByPost_whenNoComments_thenReturnEmptyList() {
      when(commentDao.findAllByPostId(TEST_POST_ID)).thenReturn(Collections.emptyList());

      List<Comment> result = commentService.getAllByPost(TEST_POST_ID);

      assertThat(result).isEmpty();
      verify(commentDao).findAllByPostId(TEST_POST_ID);
    }
  }

  @Nested
  class GetByIdTests {
    @Test
    void getById_whenCommentExists_thenReturnComment() {
      when(commentDao.find(TEST_COMMENT_ID)).thenReturn(Optional.of(testComment));

      Comment result = commentService.getById(TEST_COMMENT_ID);

      assertThat(result).isEqualTo(testComment);
      verify(commentDao).find(TEST_COMMENT_ID);
    }

    @Test
    void getById_whenCommentNotExists_thenThrowException() {
      when(commentDao.find(TEST_COMMENT_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> commentService.getById(TEST_COMMENT_ID))
          .isInstanceOf(CommentException.class)
          .hasMessageContaining("Комментарий " + TEST_COMMENT_ID + " не найден");
    }
  }

  @Nested
  class CreateTests {
    @Test
    void create_whenValidParameters_thenReturnCreatedComment() {
      when(commentDao.saveOrUpdate(any(Comment.class))).thenReturn(testComment);

      Comment result = commentService.create(testPost, testUser, TEST_BODY);

      assertThat(result).isEqualTo(testComment);
      assertThat(result.getPost()).isEqualTo(testPost);
      assertThat(result.getAuthor()).isEqualTo(testUser);
      assertThat(result.getBody()).isEqualTo(TEST_BODY);
      verify(commentDao).saveOrUpdate(any(Comment.class));
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void update_whenValidComment_thenReturnUpdatedComment() {
      String newBody = "Updated comment body";
      when(commentDao.saveOrUpdate(any(Comment.class))).thenAnswer(invocation -> {
        Comment c = invocation.getArgument(0);
        c.setBody(newBody);
        return c;
      });

      Comment result = commentService.update(testComment, newBody);

      assertThat(result.getBody()).isEqualTo(newBody);
      verify(commentDao).saveOrUpdate(testComment);
    }
  }

  @Nested
  class DeleteTests {
    @Test
    void delete_whenValidComment_thenCallDao() {
      commentService.delete(testComment);

      verify(commentDao).delete(testComment);
    }
  }
}