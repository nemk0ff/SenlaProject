package ru.senla.socialnetwork.integration.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;

public class AuthControllerImplTest extends BaseIntegrationTest {

  @Test
  @DisplayName("POST /auth/login - успешная аутентификация")
  void login_Success() throws Exception {
    AuthRequestDTO request = new AuthRequestDTO(
        "ivanov_arkadiy@senla.ru",
        "user_password"
    );

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.role").value("ROLE_USER"));
  }

  @Test
  @DisplayName("POST /auth/login - неверные учетные данные")
  void login_InvalidCredentials() throws Exception {
    AuthRequestDTO request = new AuthRequestDTO(
        "ivanov_arkadiy@senla.ru",
        "wrong_password"
    );

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("POST /auth/register - успешная регистрация")
  void register_Success() throws Exception {
    RegisterDTO registerDTO = new RegisterDTO(
        "newuser@senla.ru",
        "password123",
        "New",
        "User",
        null, null, null, null
    );

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("newuser@senla.ru"))
        .andExpect(jsonPath("$.name").value("New"))
        .andExpect(jsonPath("$.surname").value("User"));
  }

  @Test
  @DisplayName("POST /auth/register - невалидные данные (пустой email)")
  void register_InvalidData() throws Exception {
    RegisterDTO invalidRegisterDTO = new RegisterDTO(
        "",
        "password123",
        "New",
        "User",
        null, null, null, null
    );

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRegisterDTO)))
        .andExpect(status().isBadRequest());
  }
}