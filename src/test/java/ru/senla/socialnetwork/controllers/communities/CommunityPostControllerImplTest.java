package ru.senla.socialnetwork.controllers.communities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.senla.socialnetwork.controllers.communities.impl.CommunityPostControllerImpl;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.facades.communities.CommunityPostFacade;

import java.util.List;

import static ru.senla.socialnetwork.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class CommunityPostControllerImplTest {

  @Mock
  private CommunityPostFacade communityPostFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private CommunityPostControllerImpl communityPostController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private CommunityPostDTO testPost;
  private CreateCommunityPostDTO testCreateDTO;
  private UpdateCommunityPostDTO testUpdateDTO;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(communityPostController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testPost = new CommunityPostDTO(
        TEST_POST_ID,
        TEST_EMAIL_1,
        TEST_COMMUNITY_ID,
        TEST_BODY,
        TEST_DATE,
        false
    );

    testCreateDTO = new CreateCommunityPostDTO(TEST_BODY, false);
    testUpdateDTO = new UpdateCommunityPostDTO("Updated " + TEST_BODY, true);
  }

  @Nested
  class GetAllPostsTests {
    @Test
    void getAllPosts_shouldReturnPostsList() throws Exception {
      when(communityPostFacade.getAllPosts(TEST_COMMUNITY_ID))
          .thenReturn(List.of(testPost));

      mockMvc.perform(get("/communities/{communityId}/posts", TEST_COMMUNITY_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_POST_ID))
          .andExpect(jsonPath("$[0].communityId").value(TEST_COMMUNITY_ID));
    }
  }

  @Nested
  class GetPinnedPostsTests {
    @Test
    void getPinnedPosts_shouldReturnPinnedPosts() throws Exception {
      CommunityPostDTO pinnedPost = new CommunityPostDTO(
          TEST_POST_ID,
          TEST_EMAIL_1,
          TEST_COMMUNITY_ID,
          TEST_BODY,
          TEST_DATE,
          true
      );

      when(communityPostFacade.getPinnedPosts(TEST_COMMUNITY_ID))
          .thenReturn(List.of(pinnedPost));

      mockMvc.perform(get("/communities/{communityId}/posts/pinned", TEST_COMMUNITY_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].isPinned").value(true));
    }
  }

  @Nested
  class GetPostByIdTests {
    @Test
    void getById_shouldReturnPost() throws Exception {
      when(communityPostFacade.getPost(TEST_COMMUNITY_ID, TEST_POST_ID))
          .thenReturn(testPost);

      mockMvc.perform(get("/communities/{communityId}/posts/{id}", TEST_COMMUNITY_ID, TEST_POST_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_POST_ID))
          .andExpect(jsonPath("$.authorEmail").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class CreatePostTests {
    @Test
    void create_shouldReturnCreatedPost() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityPostFacade.createPost(
          eq(TEST_COMMUNITY_ID),
          any(CreateCommunityPostDTO.class),
          eq(TEST_EMAIL_1)))
          .thenReturn(testPost);

      mockMvc.perform(post("/communities/{communityId}/posts", TEST_COMMUNITY_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testCreateDTO))
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_POST_ID));
    }

    @Test
    void create_shouldReturnBadRequestForEmptyBody() throws Exception {
      CreateCommunityPostDTO invalidDTO = new CreateCommunityPostDTO("", false);

      mockMvc.perform(post("/communities/{communityId}/posts", TEST_COMMUNITY_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDTO)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class DeletePostTests {
    @Test
    void delete_shouldReturnSuccessResponse() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);

      mockMvc.perform(delete("/communities/{communityId}/posts/{postId}",
              TEST_COMMUNITY_ID, TEST_POST_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Пост сообщества успешно удалён"))
          .andExpect(jsonPath("$.data.communityId").value(TEST_COMMUNITY_ID))
          .andExpect(jsonPath("$.data.postId").value(TEST_POST_ID));
    }
  }

  @Nested
  class UpdatePostTests {
    @Test
    void update_shouldReturnUpdatedPost() throws Exception {
      CommunityPostDTO updatedPost = new CommunityPostDTO(
          TEST_POST_ID,
          TEST_EMAIL_1,
          TEST_COMMUNITY_ID,
          "Updated " + TEST_BODY,
          TEST_DATE,
          true
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityPostFacade.updatePost(
          eq(TEST_COMMUNITY_ID),
          eq(TEST_POST_ID),
          any(UpdateCommunityPostDTO.class),
          eq(TEST_EMAIL_1)))
          .thenReturn(updatedPost);

      mockMvc.perform(patch("/communities/{communityId}/posts/{postId}",
              TEST_COMMUNITY_ID, TEST_POST_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testUpdateDTO))
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.body").value("Updated " + TEST_BODY))
          .andExpect(jsonPath("$.isPinned").value(true));
    }

    @Test
    void update_shouldReturnBadRequestForEmptyBody() throws Exception {
      UpdateCommunityPostDTO invalidDTO = new UpdateCommunityPostDTO("", false);

      mockMvc.perform(patch("/communities/{communityId}/posts/{postId}",
              TEST_COMMUNITY_ID, TEST_POST_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDTO)))
          .andExpect(status().isBadRequest());
    }
  }
}