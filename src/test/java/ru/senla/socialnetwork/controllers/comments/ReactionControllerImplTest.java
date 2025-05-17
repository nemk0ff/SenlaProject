package ru.senla.socialnetwork.controllers.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.senla.socialnetwork.controllers.comments.impl.ReactionControllerImpl;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;

import java.util.List;

import static ru.senla.socialnetwork.TestConstants.*;
import ru.senla.socialnetwork.facades.comments.ReactionFacade;
import ru.senla.socialnetwork.model.comment.ReactionType;

@ExtendWith(MockitoExtension.class)
class ReactionControllerImplTest {

  @Mock
  private ReactionFacade reactionFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private ReactionControllerImpl reactionController;

  private MockMvc mockMvc;
  private ReactionDTO testReaction;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(reactionController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .build();

    testReaction = new ReactionDTO(
        TEST_REACTION_ID,
        TEST_EMAIL_1,
        TEST_COMMENT_ID,
        ReactionType.LIKE
    );
  }

  @Nested
  class GetAllReactionsTests {
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_shouldReturnReactionsList_whenAdmin() throws Exception {
      when(reactionFacade.getAll())
          .thenReturn(List.of(testReaction));

      mockMvc.perform(get("/reactions"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_REACTION_ID));
    }
  }

  @Nested
  class GetReactionByIdTests {
    @Test
    void getById_shouldReturnReaction() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(reactionFacade.getById(TEST_REACTION_ID, TEST_EMAIL_1))
          .thenReturn(testReaction);

      mockMvc.perform(get("/reactions/{id}", TEST_REACTION_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.type").value("LIKE"));
    }
  }

  @Nested
  class GetReactionsByCommentTests {
    @Test
    void getByComment_shouldReturnReactions() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(reactionFacade.getByComment(TEST_COMMENT_ID, TEST_EMAIL_1))
          .thenReturn(List.of(testReaction));

      mockMvc.perform(get("/reactions/comment/{id}", TEST_COMMENT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].commentId").value(TEST_COMMENT_ID));
    }
  }

  @Nested
  class CreateReactionTests {
    @Test
    void createReaction_shouldReturnCreatedReaction() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(reactionFacade.setReaction(TEST_COMMENT_ID, ReactionType.LIKE, TEST_EMAIL_1))
          .thenReturn(testReaction);

      mockMvc.perform(post("/reactions/comment/{id}", TEST_COMMENT_ID)
              .param("reactionType", "LIKE")
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_REACTION_ID));
    }
  }

  @Nested
  class RemoveReactionTests {
    @Test
    void removeReaction_shouldReturnSuccessResponse() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);

      mockMvc.perform(delete("/reactions/{id}", TEST_REACTION_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Реакция успешно удалена"))
          .andExpect(jsonPath("$.data.reactionId").value(TEST_REACTION_ID));
    }
  }
}