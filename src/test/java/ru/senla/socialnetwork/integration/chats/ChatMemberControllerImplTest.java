package ru.senla.socialnetwork.integration.chats;

import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class ChatMemberControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("POST /chats/{chatId}/members - Добавление участника в чат")
  class AddMemberTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен добавить участника в групповой чат (админ)")
    void shouldAddMemberToGroupChatByAdmin() throws Exception {
      mockMvc.perform(post("/chats/3/members")
              .param("email", "ivanov_arkadiy@senla.ru"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("ivanov_arkadiy@senla.ru"))
          .andExpect(jsonPath("$.chatId").value(3))
          .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при добавлении в несуществующий чат")
    void shouldReturnErrorForNonExistingChat() throws Exception {
      mockMvc.perform(post("/chats/99/members")
              .param("email", "petrova_anna@senla.ru"))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при невалидном email")
    void shouldReturnErrorForInvalidEmail() throws Exception {
      mockMvc.perform(post("/chats/1/members")
              .param("email", "invalid-email"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /chats/{chatId}/members - Удаление участника из чата")
  class RemoveMemberTests {

    @Test
    @WithMockUser(username = "fedorov_maxim@senla.ru")
    @DisplayName("Должен вернуть ошибку при удалении участника обычным пользователем")
    void shouldReturnErrorWhenRemovingMemberByRegularUser() throws Exception {
      mockMvc.perform(delete("/chats/3/members")
              .param("email", "fedorov_maxim@senla.ru"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен удалить участника из группового чата (модератор)")
    void shouldRemoveMemberFromGroupChatByModerator() throws Exception {
      mockMvc.perform(delete("/chats/3/members")
              .param("email", "fedorov_maxim@senla.ru"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("POST /chats/{chatId}/members/mute - Мут участника")
  class MuteMemberTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен замутить участника (админ)")
    void shouldMuteMemberByAdmin() throws Exception {
      String muteUntil = ZonedDateTime.now().toString();

      mockMvc.perform(post("/chats/3/members/mute")
              .param("email", "admin@senla.ru")
              .param("muteUntil", muteUntil))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("admin@senla.ru"));
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен вернуть ошибку при муте участника не админом")
    void shouldReturnErrorWhenMutingByNonAdmin() throws Exception {
      String muteUntil = "2025-05-20T12:00:00+03:00";

      mockMvc.perform(post("/chats/3/members/mute")
              .param("email", "kozlov_alexey@senla.ru")
              .param("muteUntil", muteUntil))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен вернуть ошибку при невалидной дате мута")
    void shouldReturnErrorForInvalidMuteDate() throws Exception {
      mockMvc.perform(post("/chats/3/members/mute")
              .param("email", "kozlov_alexey@senla.ru")
              .param("muteUntil", "invalid-date"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("POST /chats/{chatId}/members/unmute - Размут участника")
  class UnmuteMemberTests {
    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен вернуть ошибку при размуте участника не админом")
    void shouldReturnErrorWhenUnmutingByNonAdmin() throws Exception {
      mockMvc.perform(post("/chats/3/members/unmute")
              .param("email", "fedorov_maxim@senla.ru"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен размутить участника (админ)")
    void shouldUnmuteMemberByAdmin() throws Exception {
      mockMvc.perform(post("/chats/3/members/unmute")
              .param("email", "fedorov_maxim@senla.ru"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("fedorov_maxim@senla.ru"));
    }
  }

  @Nested
  @DisplayName("DELETE /chats/{chatId}/members/leave - Выход из чата")
  class LeaveChatTests {

    @Test
    @WithMockUser(username = "kozlov_alexey@senla.ru")
    @DisplayName("Должен позволить участнику выйти из чата")
    void shouldAllowMemberToLeaveChat() throws Exception {
      mockMvc.perform(delete("/chats/3/members/leave"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("kozlov_alexey@senla.ru"))
          .andExpect(jsonPath("$.leaveDate").exists());
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть ошибку при выходе админа из чата")
    void shouldReturnErrorWhenAdminTriesToLeave() throws Exception {
      mockMvc.perform(delete("/chats/3/members/leave"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "morozova_ekaterina@senla.ru")
    @DisplayName("Должен вернуть ошибку при выходе из несуществующего чата")
    void shouldReturnErrorForNonExistingChat() throws Exception {
      mockMvc.perform(delete("/chats/99/members/leave"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /chats/{chatId}/members/role - Изменение роли участника")
  class ChangeMemberRoleTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен изменить роль участника (админ)")
    void shouldChangeMemberRoleByAdmin() throws Exception {
      mockMvc.perform(post("/chats/3/members/role")
              .param("email", "fedorov_maxim@senla.ru")
              .param("role", "MODERATOR"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("fedorov_maxim@senla.ru"))
          .andExpect(jsonPath("$.role").value("MODERATOR"));
    }

    @Test
    @WithMockUser(username = "fedorov_maxim@senla.ru")
    @DisplayName("Должен вернуть ошибку при изменении роли не админом")
    void shouldReturnErrorWhenChangingRoleByNonAdmin() throws Exception {
      mockMvc.perform(post("/chats/3/members/role")
              .param("email", "kozlov_alexey@senla.ru")
              .param("role", "MODERATOR"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен вернуть ошибку при изменении роли несуществующего участника")
    void shouldReturnErrorForNonExistingMember() throws Exception {
      mockMvc.perform(post("/chats/3/members/role")
              .param("email", "nonexistent@senla.ru")
              .param("role", "MODERATOR"))
          .andExpect(status().isNotFound());
    }
  }
}