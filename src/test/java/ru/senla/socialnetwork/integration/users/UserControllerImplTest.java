package ru.senla.socialnetwork.integration.users;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.integration.BaseIntegrationTest;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class UserControllerImplTest extends BaseIntegrationTest {
  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /users/{id} - успешное получение пользователя")
  void getUser_ReturnsValidJson() throws Exception {
    mockMvc.perform(get("/users/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("ivanov_arkadiy@senla.ru"));
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /users/{id} - пользователь не найден")
  void getUser_NotFound() throws Exception {
    mockMvc.perform(get("/users/{id}", 999))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /users?email - успешное получение пользователя по email")
  void getUserByEmail_ReturnsValidJson() throws Exception {
    mockMvc.perform(get("/users")
            .param("email", "sidorov_dmitry@senla.ru"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(3))
        .andExpect(jsonPath("$.email").value("sidorov_dmitry@senla.ru"));
  }

  @Test
  @WithMockUser(username = "ivanov_arkadiy@senla.ru", roles = "USER")
  @DisplayName("GET /users/find - поиск по параметрам (gender и name)")
  void findUsers_ByGenderAndName() throws Exception {
    mockMvc.perform(get("/users/find")
            .param("gender", "MALE")
            .param("name", "Maxim"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Maxim"))
        .andExpect(jsonPath("$[0].gender").value("MALE"));
  }

  @Test
  @WithMockUser(username = "petrova_anna@senla.ru", roles = "USER")
  @DisplayName("PATCH /users/edit - успешное редактирование профиля")
  void editUserProfile_Success() throws Exception {
    UserRequestDTO editDTO = new UserRequestDTO(
        "Anna",
        "Petrova-Ivanova",
        null,
        Gender.FEMALE,
        ProfileType.OPEN,
        "About me"
    );

    mockMvc.perform(patch("/users/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Anna"))
        .andExpect(jsonPath("$.surname").value("Petrova-Ivanova"));
  }

  @Test
  @WithMockUser(username = "petrova_anna@senla.ru", roles = "USER")
  @DisplayName("PATCH /users/edit - невалидные данные (пустое имя)")
  void editUserProfile_InvalidName() throws Exception {
    UserRequestDTO invalidEditDTO = new UserRequestDTO(
        "",
        "Petrova",
        null,
        null,
        null,
        null
    );

    mockMvc.perform(patch("/users/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidEditDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "petrova_anna@senla.ru", roles = "USER")
  @DisplayName("PATCH /users/change-email - успешная смена email")
  void changeEmail_Success() throws Exception {
    String newEmail = "petrova@senla.ru";

    mockMvc.perform(patch("/users/change-email")
            .param("newEmail", newEmail))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.updatedUser.email").value(newEmail));
  }
}