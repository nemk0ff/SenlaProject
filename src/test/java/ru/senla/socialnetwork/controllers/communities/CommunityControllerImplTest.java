package ru.senla.socialnetwork.controllers.communities;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.senla.socialnetwork.controllers.communities.impl.CommunityControllerImpl;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.facades.communities.CommunityFacade;

import static ru.senla.socialnetwork.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class CommunityControllerImplTest {

  @Mock
  private CommunityFacade communityFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private CommunityControllerImpl communityController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private CommunityDTO testCommunity;
  private CreateCommunityDTO testCreateDTO;
  private ChangeCommunityDTO testChangeDTO;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();

    mockMvc = MockMvcBuilders.standaloneSetup(communityController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testCommunity = new CommunityDTO(
        TEST_COMMUNITY_ID,
        TEST_COMMUNITY_NAME,
        TEST_COMMUNITY_DESCRIPTION
    );

    testCreateDTO = new CreateCommunityDTO(
        TEST_COMMUNITY_NAME,
        TEST_COMMUNITY_DESCRIPTION
    );

    testChangeDTO = new ChangeCommunityDTO(
        TEST_COMMUNITY_ID,
        "Updated name",
        "Updated description"
    );
  }

  @Nested
  class CreateCommunityTests {
    @Test
    void create_shouldReturnCreatedCommunity() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityFacade.create(any(CreateCommunityDTO.class), eq(TEST_EMAIL_1)))
          .thenReturn(testCommunity);

      mockMvc.perform(post("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testCreateDTO))
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_COMMUNITY_ID))
          .andExpect(jsonPath("$.name").value(TEST_COMMUNITY_NAME));
    }

    @Test
    void create_shouldReturnBadRequestForEmptyName() throws Exception {
      CreateCommunityDTO invalidDTO = new CreateCommunityDTO("", "Description");

      mockMvc.perform(post("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDTO)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class DeleteCommunityTests {
    @Test
    void delete_shouldReturnSuccessResponse() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);

      mockMvc.perform(delete("/communities/{id}", TEST_COMMUNITY_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Сообщество успешно удалено"))
          .andExpect(jsonPath("$.data.communityId").value(TEST_COMMUNITY_ID));
    }
  }

  @Nested
  class GetCommunityTests {
    @Test
    void get_shouldReturnCommunity() throws Exception {
      when(communityFacade.get(TEST_COMMUNITY_ID))
          .thenReturn(testCommunity);

      mockMvc.perform(get("/communities/{id}", TEST_COMMUNITY_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_COMMUNITY_ID))
          .andExpect(jsonPath("$.name").value(TEST_COMMUNITY_NAME));
    }
  }

  @Nested
  class ChangeCommunityTests {
    @Test
    void change_shouldReturnUpdatedCommunity() throws Exception {
      CommunityDTO updatedCommunity = new CommunityDTO(
          TEST_COMMUNITY_ID,
          "Updated " + TEST_COMMUNITY_NAME,
          "Updated description"
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(communityFacade.change(any(ChangeCommunityDTO.class), eq(TEST_EMAIL_1)))
          .thenReturn(updatedCommunity);

      mockMvc.perform(put("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testChangeDTO))
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name").value("Updated " + TEST_COMMUNITY_NAME));
    }

    @Test
    void change_shouldReturnBadRequestForInvalidInput() throws Exception {
      ChangeCommunityDTO invalidDTO = new ChangeCommunityDTO(null, "", null);

      mockMvc.perform(put("/communities")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDTO)))
          .andExpect(status().isBadRequest());
    }
  }
}