package ru.senla.socialnetwork.unit.controllers.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.senla.socialnetwork.controllers.users.impl.WallPostControllerImpl;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.facades.wallposts.WallPostFacade;

import static ru.senla.socialnetwork.unit.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class WallPostControllerImplTest {
  @Mock
  private WallPostFacade wallPostFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private WallPostControllerImpl wallPostController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private WallPostResponseDTO testPost;
  private WallPostRequestDTO testRequestDTO;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(wallPostController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testPost = new WallPostResponseDTO(
        TEST_POST_ID,
        TEST_EMAIL_1,
        TEST_MOOD,
        TEST_BODY,
        TEST_LOCATION,
        TEST_DATE
    );

    testRequestDTO = new WallPostRequestDTO(TEST_MOOD, TEST_BODY, TEST_LOCATION);
  }

  @Nested
  class GetAllPostsTests {
    @BeforeEach
    void setUpAuth() {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
    }

    @Test
    void getAll_shouldReturnPosts() throws Exception {
      when(wallPostFacade.getByUser(TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(List.of(testPost));

      mockMvc.perform(get("/posts")
              .param("email", TEST_EMAIL_1)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_POST_ID))
          .andExpect(jsonPath("$[0].body").value(TEST_BODY));
    }
  }

  @Nested
  class GetPostByIdTests {
    @BeforeEach
    void setUpAuth() {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
    }

    @Test
    void getById_shouldReturnPost() throws Exception {
      when(wallPostFacade.getById(TEST_POST_ID, TEST_EMAIL_2)).thenReturn(testPost);

      mockMvc.perform(get("/posts/" + TEST_POST_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_POST_ID));
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
      when(wallPostFacade.getById(TEST_POST_ID, TEST_EMAIL_2))
          .thenThrow(new EntityNotFoundException("WallPost с id " + TEST_POST_ID + " не найден"));

      mockMvc.perform(get("/posts/" + TEST_POST_ID)
              .principal(authentication))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  class CreatePostTests {
    @BeforeEach
    void setUpAuth() {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
    }

    @Test
    void create_shouldReturnCreatedPost() throws Exception {
      when(wallPostFacade.create(any(WallPostRequestDTO.class), eq(TEST_EMAIL_2)))
          .thenReturn(testPost);

      mockMvc.perform(post("/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testRequestDTO))
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_POST_ID));
    }
  }

  @Nested
  class UpdatePostTests {
    @BeforeEach
    void setUpAuth() {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
    }

    @Test
    void update_shouldReturnUpdatedPost() throws Exception {
      when(wallPostFacade.update(eq(TEST_POST_ID), any(WallPostRequestDTO.class), eq(TEST_EMAIL_2)))
          .thenReturn(testPost);

      mockMvc.perform(put("/posts/" + TEST_POST_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testRequestDTO))
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_POST_ID));
    }
  }

  @Nested
  class DeletePostTests {
    @BeforeEach
    void setUpAuth() {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
    }

    @Test
    void delete_shouldReturnSuccessMessage() throws Exception {
      doNothing().when(wallPostFacade).delete(TEST_POST_ID, TEST_EMAIL_2);

      mockMvc.perform(delete("/posts/" + TEST_POST_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Пост успешно удален"))
          .andExpect(jsonPath("$.data.postId").value(TEST_POST_ID));
    }

    @Test
    void delete_shouldReturn404WhenNotFound() throws Exception {
      doThrow(new EntityNotFoundException("WallPost с id " + TEST_POST_ID + " не найден"))
          .when(wallPostFacade).delete(TEST_POST_ID, TEST_EMAIL_2);

      mockMvc.perform(delete("/posts/" + TEST_POST_ID)
              .principal(authentication))
          .andExpect(status().isNotFound());
    }
  }
}