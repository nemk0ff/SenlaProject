package ru.senla.socialnetwork.integration.feed;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class FeedControllerImplTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("GET /feed - Получение ленты новостей")
  class GetNewsFeedTests {

    @Test
    @WithMockUser(username = "ivanov_arkadiy@senla.ru")
    @DisplayName("Должен получить ленту новостей с постами друзей и сообществ")
    void shouldGetNewsFeedWithFriendsAndCommunitiesPosts() throws Exception {
      mockMvc.perform(get("/feed"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
          .andExpect(jsonPath("$[*].id").exists())
          .andExpect(jsonPath("$[*].body").exists())
          .andExpect(jsonPath("$[?(@.id == 13)].wallOwnerEmail").value("sidorov_dmitry@senla.ru"))
          .andExpect(jsonPath("$[?(@.id == 1)].authorEmail").value("ivanov_arkadiy@senla.ru"));
    }

    @Test
    @WithMockUser(username = "petrova_anna@senla.ru")
    @DisplayName("Должен получить только посты из сообществ для пользователя без друзей")
    void shouldGetOnlyCommunityPostsForUserWithoutFriends() throws Exception {
      mockMvc.perform(get("/feed"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[*].wallOwnerEmail").doesNotExist()) // Нет постов на стенах
          .andExpect(jsonPath("$[?(@.communityId)]").exists()); // Только посты из сообществ
    }
  }
}