package ru.senla.socialnetwork.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class UserControllerImplTest {

  @Container
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:15-alpine")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test")
      .withInitScripts(
          "migrations/001-initial-schema.sql",
          "migrations/002-initial-data.sql"
      );

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /users/{id} - успешное получение пользователя")
  void getUser_ReturnsValidJson() throws Exception {
    mockMvc.perform(get("/users/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("ivanov_arkadiy@senla.ru"))
        .andExpect(jsonPath("$.name").value("Arkadiy"))
        .andExpect(jsonPath("$.surname").value("Ivanov"));
  }
}