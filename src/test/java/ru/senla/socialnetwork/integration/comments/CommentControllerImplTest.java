package ru.senla.socialnetwork.integration.comments;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class CommentControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("GET /comments - Получение всех комментариев (админ)")
  class GetAllCommentsTests {

    @Test
    @WithMockUser(username = "admin@senla.ru", roles = "ADMIN")
    @DisplayName("Должен получить все комментарии (админ)")
    void shouldGetAllCommentsAsAdmin() throws Exception {
      mockMvc.perform(get("/comments"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(22)))
          .andExpect(jsonPath("$[0].id").exists())
          .andExpect(jsonPath("$[0].postId").exists())
          .andExpect(jsonPath("$[0].authorId").exists())
          .andExpect(jsonPath("$[0].body").exists());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку 403 для обычного пользователя")
    void shouldReturnForbiddenForRegularUser() throws Exception {
      mockMvc.perform(get("/comments"))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /comments/{id} - Получение комментария по ID")
  class GetCommentByIdTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен получить комментарий по ID")
    void shouldGetCommentById() throws Exception {
      mockMvc.perform(get("/comments/8"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(8))
          .andExpect(jsonPath("$.postId").value(1))
          .andExpect(jsonPath("$.authorId").value(2)) // petrova_anna@senla.ru
          .andExpect(jsonPath("$.body").value("Обновленный текст комментария"));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть 400 для несуществующего комментария")
    void shouldReturnNotFoundForNonExistingComment() throws Exception {
      mockMvc.perform(get("/comments/999"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /post/{id}/comments - Получение комментариев к посту")
  class GetPostCommentsTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен получить комментарии к посту")
    void shouldGetCommentsForPost() throws Exception {
      mockMvc.perform(get("/post/1/comments"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(3)))
          .andExpect(jsonPath("$[0].id").value(8))
          .andExpect(jsonPath("$[1].id").value(21));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть пустой список для поста без комментариев")
    void shouldReturnEmptyListForPostWithoutComments() throws Exception {
      mockMvc.perform(get("/post/9/comments")) // Пост без комментариев
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть 404 для несуществующего поста")
    void shouldReturnNotFoundForNonExistingPost() throws Exception {
      mockMvc.perform(get("/post/999/comments"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /post/{id}/comments - Создание комментария")
  class CreateCommentTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен создать комментарий к посту")
    void shouldCreateCommentForPost() throws Exception {
      CreateCommentDTO request = new CreateCommentDTO("Новый тестовый комментарий");

      mockMvc.perform(post("/post/1/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.postId").value(1))
          .andExpect(jsonPath("$.authorId").value(1)) // ivanov_arkadiy@senla.ru
          .andExpect(jsonPath("$.body").value("Новый тестовый комментарий"));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть 400 для пустого комментария")
    void shouldReturnBadRequestForEmptyComment() throws Exception {
      CreateCommentDTO request = new CreateCommentDTO("");

      mockMvc.perform(post("/post/1/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть 404 для несуществующего поста")
    void shouldReturnNotFoundForNonExistingPost() throws Exception {
      CreateCommentDTO request = new CreateCommentDTO("Комментарий");

      mockMvc.perform(post("/post/999/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("PUT /comments/{id} - Обновление комментария")
  class UpdateCommentTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен обновить комментарий (автор комментария)")
    void shouldUpdateCommentAsAuthor() throws Exception {
      UpdateCommentDTO request = new UpdateCommentDTO("Обновленный текст комментария");

      mockMvc.perform(put("/comments/8") // Комментарий petrova_anna@senla.ru
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(8))
          .andExpect(jsonPath("$.body").value("Обновленный текст комментария"));
    }

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен вернуть 400 при попытке обновить чужой комментарий")
    void shouldReturnForbiddenWhenUpdatingOthersComment() throws Exception {
      UpdateCommentDTO request = new UpdateCommentDTO("Попытка изменить");

      mockMvc.perform(put("/comments/8")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /comments/{id} - Удаление комментария")
  class DeleteCommentTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен удалить комментарий (автор комментария)")
    void shouldDeleteCommentAsAuthor() throws Exception {
      mockMvc.perform(delete("/comments/20"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Комментарий успешно удален"))
          .andExpect(jsonPath("$.data.commentId").value(20));
    }

    @Test
    @WithMockUser(username = "admin@senla.ru", roles = "ADMIN")
    @DisplayName("Должен удалить комментарий (админ)")
    void shouldDeleteCommentAsAdmin() throws Exception {
      mockMvc.perform(delete("/comments/9"))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен вернуть 400 при попытке удалить чужой комментарий")
    void shouldReturnForbiddenWhenDeletingOthersComment() throws Exception {
      mockMvc.perform(delete("/comments/8"))
          .andExpect(status().isBadRequest());
    }
  }
}