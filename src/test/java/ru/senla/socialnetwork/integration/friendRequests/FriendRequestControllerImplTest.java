package ru.senla.socialnetwork.integration.friendRequests;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public class FriendRequestControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /friends/requests - успешное получение заявок пользователя")
  void showAllByUser_Success() throws Exception {
    mockMvc.perform(get("/friends/requests")
            .param("userEmail", "ivanov_arkadiy@senla.ru"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
  }

  @Test
  @WithMockUser(username = "admin@senla.ru", roles = "ADMIN")
  @DisplayName("GET /friends/requests - успешное получение заявок пользователя (админ)")
  void showAllByUser_AdminAccess() throws Exception {
    mockMvc.perform(get("/friends/requests")
            .param("userEmail", "ivanov_arkadiy@senla.ru"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "other@senla.ru", roles = "USER")
  @DisplayName("GET /friends/requests - доступ запрещен (не тот пользователь)")
  void showAllByUser_Forbidden() throws Exception {
    mockMvc.perform(get("/friends/requests")
            .param("userEmail", "ivanov_arkadiy@senla.ru"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /friends - успешное получение списка друзей")
  void showFriends_Success() throws Exception {
    mockMvc.perform(get("/friends")
            .param("userEmail", "ivanov_arkadiy@senla.ru"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /friends/outgoing - успешное получение исходящих заявок")
  void showOutgoingRequests_Success() throws Exception {
    mockMvc.perform(get("/friends/outgoing"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(username = "petrova_anna@senla.ru", roles = "USER")
  @DisplayName("GET /friends/incoming - успешное получение входящих заявок")
  void showIncomingRequests_Success() throws Exception {
    mockMvc.perform(get("/friends/incoming")
            .param("status", "PENDING"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
  }

  @Test
  @WithMockUser(username = "sidorov_dmitry@senla.ru", roles = "USER")
  @DisplayName("POST /friends/request - успешная отправка заявки")
  void sendRequest_Success() throws Exception {
    mockMvc.perform(post("/friends/request")
            .param("recipient", "petrova_anna@senla.ru"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  @WithMockUser(username = "sidorov_dmitry@senla.ru", roles = "USER")
  @DisplayName("POST /friends/request - попытка отправить заявку самому себе")
  void sendRequest_ToSelf() throws Exception {
    mockMvc.perform(post("/friends/request")
            .param("recipient", "sidorov_dmitry@senla.ru"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "sidorov_dmitry@senla.ru", roles = "USER")
  @DisplayName("DELETE /friends/request - успешная отмена заявки")
  void cancelRequest_Success() throws Exception {
    mockMvc.perform(delete("/friends/request")
            .param("recipient", "petrova_anna@senla.ru"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  @WithMockUser(username = "petrova_anna@senla.ru", roles = "USER")
  @DisplayName("PATCH /friends/respond - успешный ответ на заявку")
  void respondRequest_Success() throws Exception {
    RespondRequestDTO request = new RespondRequestDTO(
        "ivanov_arkadiy@senla.ru",
        FriendStatus.ACCEPTED
    );

    mockMvc.perform(patch("/friends/respond")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACCEPTED"));
  }

  @Test
  @WithMockUser(username = "petrova_anna@senla.ru", roles = "USER")
  @DisplayName("PATCH /friends/respond - невалидные данные")
  void respondRequest_InvalidData() throws Exception {
    RespondRequestDTO invalidRequest = new RespondRequestDTO(
        "",
        null
    );

    mockMvc.perform(patch("/friends/respond")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("DELETE /friends/remove - успешное удаление из друзей")
  void removeFriend_Success() throws Exception {
    mockMvc.perform(delete("/friends/remove")
            .param("recipient", "sidorov_dmitry@senla.ru"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("DELETE /friends/remove - попытка удалить несуществующего друга")
  void removeFriend_NotFound() throws Exception {
    mockMvc.perform(delete("/friends/remove")
            .param("recipient", "nonexistent@senla.ru"))
        .andExpect(status().isNotFound());
  }
}