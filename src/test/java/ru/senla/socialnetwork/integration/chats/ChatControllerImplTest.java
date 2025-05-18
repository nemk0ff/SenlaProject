package ru.senla.socialnetwork.integration.chats;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import static org.hamcrest.Matchers.empty;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class ChatControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  @DisplayName("GET /chats - Получение списка чатов")
  class GetUserChatsTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть 1 личный чат для пользователя")
    void shouldReturnOnePersonalChat() throws Exception {
      mockMvc.perform(get("/chats"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].id").value(5))
          .andExpect(jsonPath("$[0].isGroup").value(true));
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть личный и групповой чаты")
    void shouldReturnPersonalAndGroupChats() throws Exception {
      mockMvc.perform(get("/chats"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "morozova_ekaterina@senla.ru")
    @DisplayName("Должен вернуть пустой список для пользователя без чатов")
    void shouldReturnEmptyListForUserWithoutChats() throws Exception {
      mockMvc.perform(get("/chats"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", empty()));
    }
  }

  @Nested
  @DisplayName("POST /chats/group - Создание группового чата")
  class CreateGroupChatTests {

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен создать новый групповой чат")
    void shouldCreateNewGroupChat() throws Exception {
      CreateGroupChatDTO request = new CreateGroupChatDTO(
          "New Admin Group",
          Set.of("ivanov_arkadiy@senla.ru", "petrova_anna@senla.ru")
      );

      mockMvc.perform(post("/chats/group")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name").value("New Admin Group"))
          .andExpect(jsonPath("$.isGroup").value(true))
          .andExpect(jsonPath("$.members").isArray())
          .andExpect(jsonPath("$.members").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при невалидных данных")
    void shouldReturnErrorForInvalidData() throws Exception {
      CreateGroupChatDTO invalidRequest = new CreateGroupChatDTO(
          "", // пустое название
          Set.of("invalid-email") // невалидный email
      );

      mockMvc.perform(post("/chats/group")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("POST /chats/personal - Создание личного чата")
  class CreatePersonalChatTests {

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен создать новый личный чат")
    void shouldCreateNewPersonalChat() throws Exception {
      mockMvc.perform(post("/chats/personal")
              .param("participant", "kozlov_alexey@senla.ru"))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.isGroup").value(false))
          .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть существующий чат при повторном создании")
    void shouldReturnExistingChat() throws Exception {
      mockMvc.perform(post("/chats/personal")
              .param("participant", "petrova_anna@senla.ru"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при создании чата с самим собой")
    void shouldReturnErrorForSelfChat() throws Exception {
      mockMvc.perform(post("/chats/personal")
              .param("participant", "ivanov_arkadiy@senla.ru"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /chats/{chatId} - Удаление чата")
  class DeleteChatTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен удалить групповой чат (админ)")
    void shouldDeleteGroupChatByAdmin() throws Exception {
      mockMvc.perform(delete("/chats/3"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Чат успешно удалён"))
          .andExpect(jsonPath("$.data.chatId").value(3));
    }

    @Test
    @WithMockUser(username = "kozlov_alexey@senla.ru")
    @DisplayName("Должен вернуть ошибку при попытке удалить не будучи админом")
    void shouldReturnErrorForNonAdmin() throws Exception {
      mockMvc.perform(delete("/chats/1"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Ошибка при действии с чатом"))
          .andExpect(jsonPath("$.detail").value("Вы не являетесь участником этого чата"));
    }

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен вернуть ошибку при удалении личного чата")
    void shouldReturnErrorForPersonalChat() throws Exception {
      mockMvc.perform(delete("/chats/2"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /chats/{chatId} - Получение информации о чате")
  class GetChatTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть личный чат")
    void shouldReturnPersonalChat() throws Exception {
      mockMvc.perform(get("/chats/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.isGroup").value(false))
          .andExpect(jsonPath("$.members.length()").value(2));
    }

    @Test
    @WithMockUser(username = "fedorov_maxim@senla.ru")
    @DisplayName("Должен вернуть групповой чат")
    void shouldReturnGroupChat() throws Exception {
      mockMvc.perform(get("/chats/3"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(3))
          .andExpect(jsonPath("$.name").value("Java Team"))
          .andExpect(jsonPath("$.isGroup").value(true))
          .andExpect(jsonPath("$.members.length()").value(4));
    }

    @Test
    @WithMockUser(username = "orlova_maria@senla.ru")
    @DisplayName("Должен вернуть 404 для несуществующего чата")
    void shouldReturnNotFoundForNonExistingChat() throws Exception {
      mockMvc.perform(get("/chats/99"))
          .andExpect(status().isNotFound());
    }
  }
}