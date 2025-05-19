package ru.senla.socialnetwork.integration.comments;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class ReactionControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("GET /reactions - Получение всех реакций (админ)")
  class GetAllReactionsTests {

    @Test
    @WithMockUser(username = "admin@senla.ru", roles = "ADMIN")
    @DisplayName("Должен получить все реакции (админ)")
    void shouldGetAllReactionsAsAdmin() throws Exception {
      mockMvc.perform(get("/reactions"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(51)))
          .andExpect(jsonPath("$[0].id").exists())
          .andExpect(jsonPath("$[0].email").exists())
          .andExpect(jsonPath("$[0].commentId").exists())
          .andExpect(jsonPath("$[0].type").exists());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть 403 для обычного пользователя")
    void shouldReturnForbiddenForRegularUser() throws Exception {
      mockMvc.perform(get("/reactions"))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /reactions/comment/{id} - Получение реакций к комментарию")
  class GetReactionsByCommentTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен получить реакции к комментарию")
    void shouldGetReactionsForComment() throws Exception {
      mockMvc.perform(get("/reactions/comment/8"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(5)))
          .andExpect(jsonPath("$[0].type").value("LIKE"))
          .andExpect(jsonPath("$[1].type").value("LIKE"));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Нет доступа")
    void shouldReturnEmptyListForCommentWithoutReactions() throws Exception {
      mockMvc.perform(get("/reactions/comment/20"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("POST /reactions/comment/{id} - Создание реакции")
  class CreateReactionTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен создать реакцию к комментарию")
    void shouldCreateReactionForComment() throws Exception {
      mockMvc.perform(post("/reactions/comment/8?reactionType=LIKE"))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.email").value("ivanov_arkadiy@senla.ru"))
          .andExpect(jsonPath("$.commentId").value(8))
          .andExpect(jsonPath("$.type").value("LIKE"));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен обновить существующую реакцию")
    void shouldUpdateExistingReaction() throws Exception {
      mockMvc.perform(post("/reactions/comment/8?reactionType=LIKE"))
          .andExpect(status().isCreated());

      mockMvc.perform(post("/reactions/comment/8?reactionType=DISLIKE"))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.type").value("DISLIKE"));
    }
  }
}