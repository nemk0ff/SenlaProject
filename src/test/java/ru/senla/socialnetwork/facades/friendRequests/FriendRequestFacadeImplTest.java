package ru.senla.socialnetwork.facades.friendRequests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.TestConstants.*;

import org.junit.jupiter.api.Nested;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.exceptions.friendRequests.SelfFriendshipException;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.user.UserService;

import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.facades.friendRequests.impl.FriendRequestFacadeImpl;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@ExtendWith(MockitoExtension.class)
class FriendRequestFacadeImplTest {
  @Mock
  private FriendRequestService friendRequestService;
  @Mock
  private UserService userService;

  @InjectMocks
  private FriendRequestFacadeImpl friendRequestFacade;

  private User testUser;
  private User friendUser;
  private FriendRequest friendRequest;
  private RespondRequestDTO respondRequestDTO;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .build();

    friendUser = User.builder()
        .id(TEST_USER_ID_2)
        .email(TEST_EMAIL_2)
        .build();

    friendRequest = FriendRequest.builder()
        .id(1L)
        .sender(testUser)
        .recipient(friendUser)
        .status(FriendStatus.PENDING)
        .build();

    respondRequestDTO = new RespondRequestDTO(
        TEST_EMAIL_1,
        FriendStatus.ACCEPTED);
  }

  @Nested
  class GetAllByUserTests {
    @Test
    void getAllByUser_whenCalled_thenReturnRequests() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.getAllByUser(testUser.getId())).thenReturn(List.of(friendRequest));

      List<FriendRequestDTO> result = friendRequestFacade.getAllByUser(TEST_EMAIL_1);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).senderEmail()).isEqualTo(TEST_EMAIL_1);
      verify(friendRequestService).getAllByUser(testUser.getId());
    }
  }

  @Nested
  class GetFriendsByUserTests {
    @Test
    void getFriendsByUser_whenCalled_thenReturnFriends() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.getFriendsByUser(testUser.getId())).thenReturn(List.of(friendUser));

      List<UserResponseDTO> result = friendRequestFacade.getFriendsByUser(TEST_EMAIL_1);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).email()).isEqualTo(TEST_EMAIL_2);
    }
  }

  @Nested
  class GetIncomingRequestsTests {
    @Test
    void getIncomingRequests_whenCalled_thenReturnRequests() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.getIncomingRequests(testUser.getId(), FriendStatus.PENDING))
          .thenReturn(List.of(friendRequest));

      List<FriendRequestDTO> result = friendRequestFacade.getIncomingRequests(TEST_EMAIL_1, FriendStatus.PENDING);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).recipientEmail()).isEqualTo(TEST_EMAIL_2);
    }
  }

  @Nested
  class GetOutgoingRequestsTests {
    @Test
    void getOutgoingRequests_whenCalled_thenReturnRequests() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(friendRequestService.getOutgoingRequests(testUser.getId()))
          .thenReturn(List.of(friendRequest));

      List<FriendRequestDTO> result = friendRequestFacade.getOutgoingRequests(TEST_EMAIL_1);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).senderEmail()).isEqualTo(TEST_EMAIL_1);
    }
  }

  @Nested
  class SendRequestTests {
    @Test
    void send_whenValid_thenReturnRequest() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(friendUser);
      when(friendRequestService.send(testUser, friendUser)).thenReturn(friendRequest);

      FriendRequestDTO result = friendRequestFacade.send(TEST_EMAIL_1, TEST_EMAIL_2);

      assertThat(result).isNotNull();
      assertThat(result.senderEmail()).isEqualTo(TEST_EMAIL_1);
    }

    @Test
    void send_whenSelfRequest_thenThrowException() {
      assertThatThrownBy(() -> friendRequestFacade.send(TEST_EMAIL_1, TEST_EMAIL_1))
          .isInstanceOf(SelfFriendshipException.class);
    }
  }

  @Nested
  class CancelRequestTests {
    @Test
    void cancel_whenValid_thenReturnRequest() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(friendUser);
      when(friendRequestService.cancel(testUser, friendUser)).thenReturn(friendRequest);

      FriendRequestDTO result = friendRequestFacade.cancel(TEST_EMAIL_1, TEST_EMAIL_2);

      assertThat(result).isNotNull();
      verify(friendRequestService).cancel(testUser, friendUser);
    }
  }

  @Nested
  class RespondRequestTests {
    @Test
    void respond_whenAccepted_thenReturnRequest() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(friendUser);
      when(friendRequestService.replyToRequest(testUser, friendUser, FriendStatus.ACCEPTED))
          .thenReturn(friendRequest);

      FriendRequestDTO result = friendRequestFacade.respond(respondRequestDTO, TEST_EMAIL_2);

      assertThat(result).isNotNull();
      assertThat(result.status()).isEqualTo(FriendStatus.PENDING); // Маппер сохраняет исходный статус
    }

    @Test
    void respond_whenInvalidStatus_thenThrowException() {
      RespondRequestDTO invalidRequest = new RespondRequestDTO(TEST_EMAIL_1, FriendStatus.PENDING);

      assertThatThrownBy(() -> friendRequestFacade.respond(invalidRequest, TEST_EMAIL_2))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Недопустимый статус");
    }
  }

  @Nested
  class UnfriendTests {
    @Test
    void unfriend_whenValid_thenReturnRequest() {
      FriendRequest unfriended = FriendRequest.builder()
          .status(FriendStatus.REJECTED)
          .build();

      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(friendUser);
      when(friendRequestService.unfriend(testUser, friendUser)).thenReturn(unfriended);

      FriendRequestDTO result = friendRequestFacade.unfriend(TEST_EMAIL_1, TEST_EMAIL_2);

      assertThat(result).isNotNull();
      assertThat(result.status()).isEqualTo(FriendStatus.REJECTED);
    }
  }
}