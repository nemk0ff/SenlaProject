package ru.senla.socialnetwork.integration.communities;

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
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class CommunityControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("POST /communities - Создание сообщества")
  class CreateCommunityTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен успешно создать сообщество")
    void shouldCreateCommunity() throws Exception {
      CreateCommunityDTO request = new CreateCommunityDTO("Новое сообщество", "Описание");

      mockMvc.perform(post("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name").value("Новое сообщество"))
          .andExpect(jsonPath("$.description").value("Описание"))
          .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при пустом названии")
    void shouldReturnErrorForEmptyName() throws Exception {
      CreateCommunityDTO request = new CreateCommunityDTO("", "Описание");

      mockMvc.perform(post("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /communities/{id} - Получение информации о сообществе")
  class GetCommunityTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен получить информацию о сообществе")
    void shouldGetCommunityInfo() throws Exception {
      mockMvc.perform(get("/communities/3"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(3))
          .andExpect(jsonPath("$.name").value("Новое название"))
          .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку для несуществующего сообщества")
    void shouldReturnErrorForNonExistingCommunity() throws Exception {
      mockMvc.perform(get("/communities/99"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("PUT /communities - Изменение сообщества")
  class UpdateCommunityTests {

    @Test
    @WithMockUser(username = "nikolaeva_olga@senla.ru")
    @DisplayName("Должен изменить сообщество (админ сообщества)")
    void shouldUpdateCommunityAsAdmin() throws Exception {
      ChangeCommunityDTO request = new ChangeCommunityDTO(3L, "Новое название", "Новое описание");

      mockMvc.perform(put("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name").value("Новое название"))
          .andExpect(jsonPath("$.description").value("Новое описание"));
    }

    @Test
    @WithMockUser(username = "nikolaeva_olga@senla.ru")
    @DisplayName("Должен изменить сообщество (админ)")
    void shouldUpdateCommunityAsSystemAdmin() throws Exception {
      ChangeCommunityDTO request = new ChangeCommunityDTO(3L, "Изменено админом", "Описание");

      mockMvc.perform(put("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("DELETE /communities/{id} - Удаление сообщества")
  class DeleteCommunityTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен удалить сообщество (админ сообщества)")
    void shouldDeleteCommunityAsAdmin() throws Exception {
      mockMvc.perform(delete("/communities/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Сообщество успешно удалено"))
          .andExpect(jsonPath("$.data.communityId").value(1));
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть ошибку при удалении не админом")
    void shouldReturnErrorWhenDeletingAsNonAdmin() throws Exception {
      mockMvc.perform(delete("/communities/1"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен удалить сообщество (системный админ)")
    void shouldDeleteCommunityAsSystemAdmin() throws Exception {
      mockMvc.perform(delete("/communities/2"))
          .andExpect(status().isOk());
    }
  }
}