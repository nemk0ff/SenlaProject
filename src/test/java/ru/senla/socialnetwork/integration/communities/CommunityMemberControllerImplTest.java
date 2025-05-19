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
import ru.senla.socialnetwork.dto.communitites.BanCommunityMemberDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class CommunityMemberControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("GET /communities/{communityId}/members - Получение списка участников")
  class GetAllMembersTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен получить список участников сообщества")
    void shouldGetCommunityMembers() throws Exception {
      mockMvc.perform(get("/communities/1/members"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].email").value("ivanov_arkadiy@senla.ru"))
          .andExpect(jsonPath("$[0].role").value("ADMIN"))
          .andExpect(jsonPath("$[1].email").value("smirnova_elena@senla.ru"))
          .andExpect(jsonPath("$[1].role").value("MEMBER"));
    }
  }

  @Nested
  @DisplayName("POST /communities/{communityId}/members - Вступление в сообщество")
  class JoinCommunityTests {

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен успешно вступить в сообщество")
    void shouldJoinCommunity() throws Exception {
      mockMvc.perform(post("/communities/3/members"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("petrova_anna@senla.ru"))
          .andExpect(jsonPath("$.communityId").value(3))
          .andExpect(jsonPath("$.role").value("MEMBER"));
    }
  }

  @Nested
  @DisplayName("DELETE /communities/{communityId}/members - Выход из сообщества")
  class LeaveCommunityTests {

    @Test
    @WithMockUser(username = "sidorov_dmitry@senla.ru")
    @DisplayName("Должен успешно выйти из сообщества")
    void shouldLeaveCommunity() throws Exception {
      mockMvc.perform(delete("/communities/1/members"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("sidorov_dmitry@senla.ru"))
          .andExpect(jsonPath("$.communityId").value(1));
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть ошибку при выходе из сообщества, в котором не состоит")
    void shouldReturnErrorWhenNotMember() throws Exception {
      mockMvc.perform(delete("/communities/1/members"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /communities/{communityId}/members/ban - Блокировка участника")
  class BanMemberTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен успешно заблокировать участника (админ сообщества)")
    void shouldBanMemberAsCommunityAdmin() throws Exception {
      BanCommunityMemberDTO request = new BanCommunityMemberDTO("sidorov_dmitry@senla.ru", "Нарушение правил");

      mockMvc.perform(post("/communities/1/members/ban")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("sidorov_dmitry@senla.ru"))
          .andExpect(jsonPath("$.isBanned").value(true))
          .andExpect(jsonPath("$.bannedReason").value("Нарушение правил"));
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен вернуть ошибку при блокировке не админом")
    void shouldReturnErrorWhenBanningAsNonAdmin() throws Exception {
      BanCommunityMemberDTO request = new BanCommunityMemberDTO("sidorov_dmitry@senla.ru", "Причина");

      mockMvc.perform(post("/communities/1/members/ban")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при блокировке несуществующего участника")
    void shouldReturnErrorWhenBanningNonExistingMember() throws Exception {
      BanCommunityMemberDTO request = new BanCommunityMemberDTO("nonexisting@senla.ru", "Причина");

      mockMvc.perform(post("/communities/1/members/ban")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /communities/{communityId}/members/unban - Разблокировка участника")
  class UnbanMemberTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен успешно разблокировать участника (админ сообщества)")
    void shouldUnbanMemberAsCommunityAdmin() throws Exception {
      mockMvc.perform(post("/communities/1/members/unban")
              .param("email", "smirnova_elena@senla.ru"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("smirnova_elena@senla.ru"))
          .andExpect(jsonPath("$.isBanned").value(false));
    }

    @Test
    @WithMockUser(username = "admin@senla.ru")
    @DisplayName("Должен успешно разблокировать участника (системный админ)")
    void shouldUnbanMemberAsSystemAdmin() throws Exception {
      mockMvc.perform(post("/communities/2/members/unban")
              .param("email", "fedorov_maxim@senla.ru"))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "smirnova_elena@senla.ru")
    @DisplayName("Должен вернуть ошибку при разблокировке не админом")
    void shouldReturnErrorWhenUnbanningAsNonAdmin() throws Exception {
      mockMvc.perform(post("/communities/1/members/unban")
              .param("email", "smirnova_elena@senla.ru"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PATCH /communities/{communityId}/members/role - Изменение роли участника")
  class ChangeMemberRoleTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен успешно изменить роль участника (админ сообщества)")
    void shouldChangeMemberRoleAsCommunityAdmin() throws Exception {
      mockMvc.perform(patch("/communities/1/members/role")
              .param("email", "sidorov_dmitry@senla.ru")
              .param("role", "MODERATOR"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("sidorov_dmitry@senla.ru"))
          .andExpect(jsonPath("$.role").value("MODERATOR"));
    }

    @Test
    @WithMockUser(username = "smirnova_elena@senla.ru")
    @DisplayName("Должен вернуть ошибку при изменении роли не админом")
    void shouldReturnErrorWhenChangingRoleAsNonAdmin() throws Exception {
      mockMvc.perform(patch("/communities/1/members/role")
              .param("email", "sidorov_dmitry@senla.ru")
              .param("role", "MODERATOR"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен вернуть ошибку при изменении своей роли")
    void shouldReturnErrorWhenChangingOwnRole() throws Exception {
      mockMvc.perform(patch("/communities/1/members/role")
              .param("email", "ivanov_arkadiy@senla.ru")
              .param("role", "MEMBER"))
          .andExpect(status().isBadRequest());
    }
  }
}