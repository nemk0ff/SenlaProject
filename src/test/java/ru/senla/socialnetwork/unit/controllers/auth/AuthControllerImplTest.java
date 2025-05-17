package ru.senla.socialnetwork.unit.controllers.auth;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.controllers.auth.AuthControllerImpl;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.AuthResponseDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.model.users.UserRole;
import ru.senla.socialnetwork.services.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerImplTest {
  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthControllerImpl authController;

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(authController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .build();
  }

  @Nested
  class LoginTests {
    @Test
    void login_shouldReturnTokenAndRole_whenCredentialsValid() throws Exception {
      AuthRequestDTO request = new AuthRequestDTO(TEST_EMAIL_1, TEST_PASSWORD);
      AuthResponseDTO response = new AuthResponseDTO("ROLE_USER", "jwt.token.123");

      when(authService.getAuthResponse(request)).thenReturn(response);

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value("jwt.token.123"))
          .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void login_shouldReturnBadRequest_whenEmailInvalid() throws Exception {
      AuthRequestDTO invalidRequest = new AuthRequestDTO("invalid-email", TEST_PASSWORD);

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class RegisterTests {
    @Test
    void register_shouldReturnUserResponse_whenRegistrationSuccessful() throws Exception {
      RegisterDTO request = new RegisterDTO(
          TEST_EMAIL_1, TEST_PASSWORD, TEST_NAME, TEST_SURNAME,
          null, null, null, null);

      UserResponseDTO response = new UserResponseDTO(
          1L, TEST_EMAIL_1, UserRole.USER, TEST_NAME, TEST_SURNAME,
          null, null, null, TEST_PROFILE_TYPE, ZonedDateTime.now());

      when(authService.register(request)).thenReturn(response);

      mockMvc.perform(post("/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }

    @Test
    void register_shouldHandleUserAlreadyExists() throws Exception {
      RegisterDTO request = new RegisterDTO(
          TEST_EMAIL_1, TEST_PASSWORD, TEST_NAME, TEST_SURNAME,
          null, null, null, null);

      when(authService.register(request))
          .thenThrow(new EmailAlreadyExistsException(TEST_EMAIL_1));

      mockMvc.perform(post("/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value("Ошибка при попытке присвоения email"))
          .andExpect(jsonPath("$.detail").value(TEST_EMAIL_1 + " уже используется"))
          .andExpect(jsonPath("$.path").value("/auth/register"));
    }
  }
}