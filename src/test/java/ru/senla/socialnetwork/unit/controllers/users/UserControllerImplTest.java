package ru.senla.socialnetwork.unit.controllers.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.senla.socialnetwork.controllers.users.impl.UserControllerImpl;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.UserRole;
import ru.senla.socialnetwork.security.JwtUtils;
import ru.senla.socialnetwork.services.user.UserService;

import static ru.senla.socialnetwork.unit.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

  @Mock
  private UserService userService;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private UserControllerImpl userController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private User testUser;
  private UserRequestDTO editDTO;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .name(TEST_NAME)
        .surname(TEST_SURNAME)
        .birthDate(TEST_BIRTHDATE)
        .gender(TEST_GENDER)
        .aboutMe(TEST_ABOUT_ME)
        .profileType(TEST_PROFILE_TYPE)
        .role(UserRole.USER)
        .build();

    editDTO = new UserRequestDTO(
        "Updated Name",
        "Updated Surname",
        TEST_BIRTHDATE,
        TEST_GENDER,
        TEST_PROFILE_TYPE,
        TEST_ABOUT_ME
    );
  }

  @Nested
  class GetUserTests {
    @Test
    void getByEmail_shouldReturnUser() throws Exception {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);

      mockMvc.perform(get("/users").param("email", TEST_EMAIL_1))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class ChangeEmailTests {
    @Test
    void changeEmail_shouldReturnNewTokenAndUser() throws Exception {
      User updatedUser = User.builder()
          .id(TEST_USER_ID_1)
          .email(TEST_EMAIL_2)
          .role(UserRole.USER)
          .build();

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(userService.changeEmail(TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(updatedUser);

      try (MockedStatic<JwtUtils> mocked = mockStatic(JwtUtils.class)) {
        mocked.when(() -> JwtUtils.generateToken(anyString(), anyString()))
            .thenReturn("mock.token");

        mockMvc.perform(patch("/users/change-email")
                .param("newEmail", TEST_EMAIL_2)
                .principal(authentication))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());
      }
    }
  }

  @Nested
  class EditUserTests {
    @Test
    void editUser_shouldUpdateAndReturnUser() throws Exception {
      User updatedUser = User.builder()
          .id(TEST_USER_ID_1)
          .email(TEST_EMAIL_1)
          .name(editDTO.name())
          .build();

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(userService.edit(any(UserRequestDTO.class), eq(TEST_EMAIL_1)))
          .thenReturn(updatedUser);

      mockMvc.perform(patch("/users/edit")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(editDTO))
              .principal(authentication))
          .andExpect(status().isOk());
    }
  }
}