package ru.senla.socialnetwork.integration.communities;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class CommunityPostControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("GET /communities/{communityId}/posts - Получение всех постов")
  class GetAllPostsTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен получить все посты сообщества")
    void shouldGetAllCommunityPosts() throws Exception {
      mockMvc.perform(get("/communities/3/posts"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].id").exists())
          .andExpect(jsonPath("$[0].communityId").value(3))
          .andExpect(jsonPath("$[0].body").exists());
    }
  }

  @Nested
  @DisplayName("GET /communities/{communityId}/posts/pinned - Получение закрепленных постов")
  class GetPinnedPostsTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен получить закрепленные посты сообщества")
    void shouldGetPinnedPosts() throws Exception {
      mockMvc.perform(get("/communities/3/posts/pinned"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].isPinned").value(true));
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть пустой список если нет закрепленных постов")
    void shouldReturnEmptyListWhenNoPinnedPosts() throws Exception {
      mockMvc.perform(get("/communities/2/posts/pinned"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /communities/{communityId}/posts/{id} - Получение поста по ID")
  class GetPostByIdTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен получить пост по ID")
    void shouldGetPostById() throws Exception {
      mockMvc.perform(get("/communities/3/posts/8"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(8))
          .andExpect(jsonPath("$.communityId").value(3))
          .andExpect(jsonPath("$.body").exists());
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть ошибку для несуществующего поста")
    void shouldReturnErrorForNonExistingPost() throws Exception {
      mockMvc.perform(get("/communities/1/posts/99"))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть ошибку если пост не принадлежит сообществу")
    void shouldReturnErrorWhenPostNotInCommunity() throws Exception {
      mockMvc.perform(get("/communities/2/posts/1"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /communities/{communityId}/posts - Создание поста")
  class CreatePostTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен успешно создать пост (участник сообщества)")
    void shouldCreatePostAsMember() throws Exception {
      CreateCommunityPostDTO request = new CreateCommunityPostDTO("Новый пост", false);

      mockMvc.perform(post("/communities/1/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.body").value("Новый пост"))
          .andExpect(jsonPath("$.authorEmail").value("ivanov_arkadiy@senla.ru"))
          .andExpect(jsonPath("$.communityId").value(1));
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть ошибку при создании поста в чужом сообществе")
    void shouldReturnErrorWhenCreatingPostInNotJoinedCommunity() throws Exception {
      CreateCommunityPostDTO request = new CreateCommunityPostDTO("Новый пост", false);

      mockMvc.perform(post("/communities/1/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при пустом теле поста")
    void shouldReturnErrorForEmptyPostBody() throws Exception {
      CreateCommunityPostDTO request = new CreateCommunityPostDTO("", false);

      mockMvc.perform(post("/communities/1/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /communities/{communityId}/posts/{postId} - Удаление поста")
  class DeletePostTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен успешно удалить пост (автор поста)")
    void shouldDeletePostAsAuthor() throws Exception {
      mockMvc.perform(delete("/communities/1/posts/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Пост сообщества успешно удалён"))
          .andExpect(jsonPath("$.data.communityId").value(1))
          .andExpect(jsonPath("$.data.postId").value(1));
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен успешно удалить пост (системный админ)")
    void shouldDeletePostAsSystemAdmin() throws Exception {
      mockMvc.perform(delete("/communities/1/posts/2"))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен вернуть ошибку при удалении чужого поста")
    void shouldReturnErrorWhenDeletingOthersPost() throws Exception {
      mockMvc.perform(delete("/communities/2/posts/3"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("PATCH /communities/{communityId}/posts/{postId} - Обновление поста")
  class UpdatePostTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен успешно обновить пост (автор поста)")
    void shouldUpdatePostAsAuthor() throws Exception {
      UpdateCommunityPostDTO request = new UpdateCommunityPostDTO("Обновленный текст", true);

      mockMvc.perform(patch("/communities/1/posts/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.body").value("Обновленный текст"))
          .andExpect(jsonPath("$.isPinned").value(true));
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен успешно обновить пост (системный админ)")
    void shouldUpdatePostAsSystemAdmin() throws Exception {
      UpdateCommunityPostDTO request = new UpdateCommunityPostDTO("Обновленный админом", false);

      mockMvc.perform(patch("/communities/2/posts/6")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "smirnova_elena@senla.ru")
    @DisplayName("Должен вернуть ошибку при обновлении чужого поста")
    void shouldReturnErrorWhenUpdatingOthersPost() throws Exception {
      UpdateCommunityPostDTO request = new UpdateCommunityPostDTO("Попытка изменить", false);

      mockMvc.perform(patch("/communities/1/posts/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при пустом теле поста")
    void shouldReturnErrorForEmptyPostBody() throws Exception {
      UpdateCommunityPostDTO request = new UpdateCommunityPostDTO("", false);

      mockMvc.perform(patch("/communities/1/posts/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }
}