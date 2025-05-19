package ru.senla.socialnetwork.unit.controllers.friendRequests;

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
import ru.senla.socialnetwork.controllers.friendRequests.impl.FriendRequestControllerImpl;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;

import java.util.List;

import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.facades.friendRequests.FriendRequestFacade;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@ExtendWith(MockitoExtension.class)
class FriendRequestControllerImplTest {
  @Mock
  private FriendRequestFacade friendRequestFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private FriendRequestControllerImpl friendRequestController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private FriendRequestDTO testRequest;
  private UserResponseDTO testUserResponse;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(friendRequestController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testRequest = new FriendRequestDTO(
        TEST_USER_ID_1,
        TEST_EMAIL_1,
        TEST_EMAIL_2,
        TEST_DATE,
        FriendStatus.PENDING
    );

    testUserResponse = new UserResponseDTO(
        TEST_USER_ID_1,
        TEST_EMAIL_1,
        TEST_ROLE,
        TEST_NAME,
        TEST_SURNAME,
        TEST_BIRTHDATE,
        TEST_GENDER,
        TEST_ABOUT_ME,
        TEST_PROFILE_TYPE,
        TEST_DATE
    );
  }

  @Nested
  class ShowAllByUserTests {
    @Test
    void showAllByUser_shouldReturnRequests() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(friendRequestFacade.getAllByUser(TEST_EMAIL_1))
          .thenReturn(List.of(testRequest));

      mockMvc.perform(get("/friends/requests")
              .param("userEmail", TEST_EMAIL_1)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_USER_ID_1))
          .andExpect(jsonPath("$[0].senderEmail").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class ShowFriendsTests {
    @Test
    void showFriends_shouldReturnFriendsList() throws Exception {
      when(friendRequestFacade.getFriendsByUser(TEST_EMAIL_1))
          .thenReturn(List.of(testUserResponse));

      mockMvc.perform(get("/friends")
              .param("userEmail", TEST_EMAIL_1))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].email").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class ShowOutgoingRequestsTests {
    @Test
    void showOutgoingRequests_shouldReturnOutgoingRequests() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(friendRequestFacade.getOutgoingRequests(TEST_EMAIL_1))
          .thenReturn(List.of(testRequest));

      mockMvc.perform(get("/friends/outgoing")
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].senderEmail").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class ShowIncomingRequestsTests {
    @Test
    void showIncomingRequests_shouldReturnIncomingRequests() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(friendRequestFacade.getIncomingRequests(TEST_EMAIL_1, FriendStatus.PENDING))
          .thenReturn(List.of(testRequest));

      mockMvc.perform(get("/friends/incoming")
              .param("status", "PENDING")
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].recipientEmail").value(TEST_EMAIL_2));
    }
  }

  @Nested
  class SendRequestTests {
    @Test
    void sendRequest_shouldReturnCreatedRequest() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(friendRequestFacade.send(TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(testRequest);

      mockMvc.perform(post("/friends/request")
              .param("recipient", TEST_EMAIL_2)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_USER_ID_1));
    }
  }

  @Nested
  class CancelRequestTests {
    @Test
    void cancelRequest_shouldReturnCancelledRequest() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(friendRequestFacade.cancel(TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(testRequest);

      mockMvc.perform(delete("/friends/request")
              .param("recipient", TEST_EMAIL_2)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_USER_ID_1));
    }
  }

  @Nested
  class RespondRequestTests {
    private RespondRequestDTO testRespondRequest;

    @BeforeEach
    void setUpRespondRequest() {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      testRespondRequest = new RespondRequestDTO(TEST_EMAIL_2, FriendStatus.ACCEPTED);
    }

    @Test
    void respondRequest_shouldReturnUpdatedRequest() throws Exception {
      FriendRequestDTO acceptedRequest = new FriendRequestDTO(
          TEST_USER_ID_1,
          TEST_EMAIL_2,
          TEST_EMAIL_1,
          TEST_DATE,
          FriendStatus.ACCEPTED
      );

      when(friendRequestFacade.respond(any(RespondRequestDTO.class), eq(TEST_EMAIL_1)))
          .thenReturn(acceptedRequest);

      mockMvc.perform(patch("/friends/respond")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testRespondRequest))
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
  }

  @Nested
  class RemoveFriendTests {
    @Test
    void removeFriend_shouldReturnRemovedFriend() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      FriendRequestDTO removedFriend = new FriendRequestDTO(
          TEST_USER_ID_1,
          TEST_EMAIL_1,
          TEST_EMAIL_2,
          TEST_DATE,
          FriendStatus.CANCELLED
      );

      when(friendRequestFacade.unfriend(TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(removedFriend);

      mockMvc.perform(delete("/friends/remove")
              .param("recipient", TEST_EMAIL_2)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
  }
}