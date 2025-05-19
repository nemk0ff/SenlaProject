package ru.senla.socialnetwork.integration.chats;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class MessageControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("POST /chats/{chatId}/messages - Отправка сообщения")
  class SendMessageTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен успешно отправить сообщение (админ чата)")
    void shouldSendMessageAsAdmin() throws Exception {
      MessageRequestDTO request = new MessageRequestDTO("Новое сообщение от админа", null);

      mockMvc.perform(post("/chats/3/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.body").value("Новое сообщение от админа"))
          .andExpect(jsonPath("$.authorEmail").value("petrova_anna@senla.ru"));
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен успешно отправить сообщение (участник чата)")
    void shouldSendMessageAsMember() throws Exception {
      MessageRequestDTO request = new MessageRequestDTO("Сообщение от админа проекта", null);

      mockMvc.perform(post("/chats/3/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен вернуть ошибку при отправке (не участник чата)")
    void shouldReturnErrorForNonMember() throws Exception {
      MessageRequestDTO request = new MessageRequestDTO("Попытка отправить", null);

      mockMvc.perform(post("/chats/3/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.detail").value("Участник sidorov_dmitry@senla.ru чата id= 3 не найден"));
    }
  }

  @Nested
  @DisplayName("GET /chats/{chatId}/messages - Получение сообщений чата")
  class GetMessagesTests {

    @Test
    @WithMockUser(username = "fedorov_maxim@senla.ru")
    @DisplayName("Должен получить 4 сообщения из группового чата")
    void shouldGetAllMessages() throws Exception {
      mockMvc.perform(get("/chats/3/messages"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(4)))
          .andExpect(jsonPath("$[*].id", containsInAnyOrder(4, 5, 6, 7)))
          .andExpect(jsonPath("$[*].body", containsInAnyOrder(
              "Ребята, кто разбирается в Spring Security?",
              "Я могу помочь",
              "У меня тоже есть опыт",
              "Давайте созвонимся завтра)"
          )));
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен получить сообщения (системный админ)")
    void shouldGetMessagesAsSystemAdmin() throws Exception {
      mockMvc.perform(get("/chats/3/messages"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку (не участник чата)")
    void shouldReturnErrorForNonMember() throws Exception {
      mockMvc.perform(get("/chats/3/messages"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.detail").value("Участник ivanov_arkadiy@senla.ru чата id= 3 не найден"));
    }
  }

  @Nested
  @DisplayName("GET /chats/{chatId}/messages/pinned - Закрепленные сообщения")
  class GetPinnedMessagesTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен получить 1 закрепленное сообщение")
    void shouldGetPinnedMessage() throws Exception {
      mockMvc.perform(get("/chats/3/messages/pinned"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].id").value(7))
          .andExpect(jsonPath("$[0].body").value("Давайте созвонимся завтра)"))
          .andExpect(jsonPath("$[0].isPinned").value(true));
    }

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен получить пустой список при отсутствии закрепленных")
    void shouldReturnEmptyForNoPinned() throws Exception {
      mockMvc.perform(get("/chats/2/messages/pinned"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.detail").value("В чате нет закреплённых сообщений"));

    }
  }
}