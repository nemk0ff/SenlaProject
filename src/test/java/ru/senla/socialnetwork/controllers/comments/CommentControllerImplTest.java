package ru.senla.socialnetwork.controllers.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.senla.socialnetwork.controllers.comments.impl.CommentControllerImpl;
import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.facades.comments.CommentFacade;

import java.util.List;

import static ru.senla.socialnetwork.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerImplTest {

  @Mock
  private CommentFacade commentFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private CommentControllerImpl commentController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private CommentDTO testComment;
  private CreateCommentDTO testCreateDTO;
  private UpdateCommentDTO testUpdateDTO;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();

    mockMvc = MockMvcBuilders.standaloneSetup(commentController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testComment = new CommentDTO(
        TEST_COMMENT_ID,
        TEST_POST_ID,
        TEST_USER_ID_1,
        TEST_BODY
    );

    testCreateDTO = new CreateCommentDTO(TEST_BODY);
    testUpdateDTO = new UpdateCommentDTO("Updated " + TEST_BODY);
  }

  @Nested
  class GetAllCommentsTests {
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_shouldReturnCommentsList_whenAdmin() throws Exception {
      when(commentFacade.getAll())
          .thenReturn(List.of(testComment));

      mockMvc.perform(get("/comments"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_COMMENT_ID));
    }
  }

  @Nested
  class GetCommentByIdTests {
    @Test
    void getById_shouldReturnComment() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(commentFacade.getById(TEST_COMMENT_ID, TEST_EMAIL_1))
          .thenReturn(testComment);

      mockMvc.perform(get("/comments/{id}", TEST_COMMENT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.body").value(TEST_BODY));
    }
  }

  @Nested
  class GetPostCommentsTests {
    @Test
    void getPostComments_shouldReturnComments() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(commentFacade.getPostComments(TEST_POST_ID, TEST_EMAIL_1))
          .thenReturn(List.of(testComment));

      mockMvc.perform(get("/post/{id}/comments", TEST_POST_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].postId").value(TEST_POST_ID));
    }
  }

  @Nested
  class CreateCommentTests {
    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(commentFacade.create(eq(TEST_POST_ID), any(CreateCommentDTO.class), eq(TEST_EMAIL_1)))
          .thenReturn(testComment);

      mockMvc.perform(post("/post/{id}/comments", TEST_POST_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testCreateDTO))
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_COMMENT_ID));
    }

    @Test
    void createComment_shouldReturnBadRequestForEmptyBody() throws Exception {
      CreateCommentDTO invalidDTO = new CreateCommentDTO("");

      mockMvc.perform(post("/post/{id}/comments", TEST_POST_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDTO)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class UpdateCommentTests {
    @Test
    void updateComment_shouldReturnUpdatedComment() throws Exception {
      CommentDTO updatedComment = new CommentDTO(
          TEST_COMMENT_ID,
          TEST_POST_ID,
          TEST_USER_ID_1,
          "Updated " + TEST_BODY
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(commentFacade.update(eq(TEST_COMMENT_ID), any(UpdateCommentDTO.class), eq(TEST_EMAIL_1)))
          .thenReturn(updatedComment);

      mockMvc.perform(put("/comments/{id}", TEST_COMMENT_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testUpdateDTO))
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.body").value("Updated " + TEST_BODY));
    }
  }

  @Nested
  class DeleteCommentTests {
    @Test
    void deleteComment_shouldReturnSuccessResponse() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);

      mockMvc.perform(delete("/comments/{id}", TEST_COMMENT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Комментарий успешно удален"))
          .andExpect(jsonPath("$.data.commentId").value(TEST_COMMENT_ID));
    }
  }
}