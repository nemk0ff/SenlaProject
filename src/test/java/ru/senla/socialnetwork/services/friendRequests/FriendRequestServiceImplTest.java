package ru.senla.socialnetwork.services.friendRequests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.socialnetwork.dao.friendRequests.FriendRequestDao;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadyFriendsException;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadySentException;
import ru.senla.socialnetwork.exceptions.friendRequests.FriendRequestException;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;
import ru.senla.socialnetwork.model.users.User;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static ru.senla.socialnetwork.TestConstants.*;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestServiceImpl;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceImplTest {
  @Mock
  private FriendRequestDao friendRequestDao;

  @InjectMocks
  private FriendRequestServiceImpl friendRequestService;

  private User testUser1;
  private User testUser2;
  private FriendRequest testRequest;

  @BeforeEach
  void setUp() {
    testUser1 = User.builder()
        .id(TEST_USER_ID)
        .email(TEST_EMAIL_1)
        .name(TEST_NAME)
        .build();

    testUser2 = User.builder()
        .id(2L)
        .email(TEST_EMAIL_2)
        .name("Test User 2")
        .build();

    testRequest = FriendRequest.builder()
        .id(1L)
        .sender(testUser1)
        .recipient(testUser2)
        .status(FriendStatus.PENDING)
        .createdAt(ZonedDateTime.now())
        .build();
  }

  @Nested
  class GetAllByUserTests {
    @Test
    void getAllByUser_whenRequestsExist_thenReturnRequests() {
      when(friendRequestDao.getAllByUserId(TEST_USER_ID)).thenReturn(List.of(testRequest));

      List<FriendRequest> result = friendRequestService.getAllByUser(TEST_USER_ID);

      assertThat(result).containsExactly(testRequest);
      verify(friendRequestDao).getAllByUserId(TEST_USER_ID);
    }

    @Test
    void getAllByUser_whenNoRequests_thenReturnEmptyList() {
      when(friendRequestDao.getAllByUserId(TEST_USER_ID)).thenReturn(List.of());

      List<FriendRequest> result = friendRequestService.getAllByUser(TEST_USER_ID);

      assertThat(result).isEmpty();
      verify(friendRequestDao).getAllByUserId(TEST_USER_ID);
    }
  }

  @Nested
  class GetFriendsByUserTests {
    @Test
    void getFriendsByUser_whenFriendsExist_thenReturnFriends() {
      when(friendRequestDao.findFriendsByUserId(TEST_USER_ID)).thenReturn(List.of(testUser2));

      List<User> result = friendRequestService.getFriendsByUser(TEST_USER_ID);

      assertThat(result).containsExactly(testUser2);
      verify(friendRequestDao).findFriendsByUserId(TEST_USER_ID);
    }

    @Test
    void getFriendsByUser_whenNoFriends_thenReturnEmptyList() {
      when(friendRequestDao.findFriendsByUserId(TEST_USER_ID)).thenReturn(List.of());

      List<User> result = friendRequestService.getFriendsByUser(TEST_USER_ID);

      assertThat(result).isEmpty();
      verify(friendRequestDao).findFriendsByUserId(TEST_USER_ID);
    }
  }

  @Nested
  class GetIncomingRequestsTests {
    @Test
    void getIncomingRequests_whenRequestsExist_thenReturnRequests() {
      User recipient = new User();
      recipient.setId(TEST_USER_ID);
      testRequest.setRecipient(recipient);
      testRequest.setStatus(FriendStatus.PENDING);
      when(friendRequestDao.getAllByUserId(TEST_USER_ID))
          .thenReturn(List.of(testRequest));

      List<FriendRequest> result = friendRequestService.getIncomingRequests(
          TEST_USER_ID, FriendStatus.PENDING);

      assertThat(result)
          .containsExactly(testRequest);
      verify(friendRequestDao).getAllByUserId(TEST_USER_ID);
    }

    @Test
    void getIncomingRequests_whenNoRequests_thenReturnEmptyList() {
      when(friendRequestDao.getAllByUserId(TEST_USER_ID)).thenReturn(List.of());

      List<FriendRequest> result = friendRequestService.getIncomingRequests(
          TEST_USER_ID, FriendStatus.PENDING);

      assertThat(result).isEmpty();
      verify(friendRequestDao).getAllByUserId(TEST_USER_ID);
    }
  }

  @Nested
  class GetOutgoingRequestsTests {
    @Test
    void getOutgoingRequests_whenRequestsExist_thenReturnRequests() {
      when(friendRequestDao.getAllByUserId(TEST_USER_ID)).thenReturn(List.of(testRequest));

      List<FriendRequest> result = friendRequestService.getOutgoingRequests(TEST_USER_ID);

      assertThat(result).containsExactly(testRequest);
      verify(friendRequestDao).getAllByUserId(TEST_USER_ID);
    }

    @Test
    void getOutgoingRequests_whenNoRequests_thenReturnEmptyList() {
      when(friendRequestDao.getAllByUserId(TEST_USER_ID)).thenReturn(List.of());

      List<FriendRequest> result = friendRequestService.getOutgoingRequests(TEST_USER_ID);

      assertThat(result).isEmpty();
      verify(friendRequestDao).getAllByUserId(TEST_USER_ID);
    }
  }

  @Nested
  class SendTests {
    @Test
    void send_whenNoExistingRequest_thenCreateNew() {
      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, false))
          .thenReturn(Optional.empty());
      when(friendRequestDao.saveOrUpdate(any(FriendRequest.class)))
          .thenReturn(testRequest);

      FriendRequest result = friendRequestService.send(testUser1, testUser2);

      assertThat(result).isEqualTo(testRequest);
      verify(friendRequestDao).saveOrUpdate(any(FriendRequest.class));
    }

    @Test
    void send_whenAlreadyAccepted_thenThrowException() {
      FriendRequest acceptedRequest = FriendRequest.builder()
          .sender(testUser2)
          .recipient(testUser1)
          .status(FriendStatus.ACCEPTED)
          .build();

      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, false))
          .thenReturn(Optional.of(acceptedRequest));

      assertThatThrownBy(() -> friendRequestService.send(testUser1, testUser2))
          .isInstanceOf(AlreadyFriendsException.class)
          .hasMessageContaining(testUser2.getEmail());
    }

    @Test
    void send_whenPendingFromSameUser_thenThrowException() {
      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, false))
          .thenReturn(Optional.of(testRequest));

      assertThatThrownBy(() -> friendRequestService.send(testUser1, testUser2))
          .isInstanceOf(AlreadySentException.class)
          .hasMessageContaining(testUser2.getEmail());
    }

    @Test
    void send_whenRejectedRequest_thenUpdateToPending() {
      FriendRequest rejectedRequest = FriendRequest.builder()
          .sender(testUser1)
          .recipient(testUser2)
          .status(FriendStatus.REJECTED)
          .build();

      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, false))
          .thenReturn(Optional.of(rejectedRequest));
      when(friendRequestDao.saveOrUpdate(any(FriendRequest.class)))
          .thenAnswer(invocation -> {
            FriendRequest r = invocation.getArgument(0);
            r.setStatus(FriendStatus.PENDING);
            return r;
          });

      FriendRequest result = friendRequestService.send(testUser1, testUser2);

      assertThat(result.getStatus()).isEqualTo(FriendStatus.PENDING);
      verify(friendRequestDao).saveOrUpdate(rejectedRequest);
    }

    @Test
    void send_whenRecipientHasPendingRequest_thenAccept() {
      FriendRequest reverseRequest = FriendRequest.builder()
          .sender(testUser2)
          .recipient(testUser1)
          .status(FriendStatus.PENDING)
          .build();

      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, false))
          .thenReturn(Optional.of(reverseRequest));
      when(friendRequestDao.saveOrUpdate(any(FriendRequest.class)))
          .thenAnswer(invocation -> {
            FriendRequest r = invocation.getArgument(0);
            r.setStatus(FriendStatus.ACCEPTED);
            return r;
          });

      FriendRequest result = friendRequestService.send(testUser1, testUser2);

      assertThat(result.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
      verify(friendRequestDao).saveOrUpdate(reverseRequest);
    }
  }

  @Nested
  class CancelTests {
    @Test
    void cancel_whenRequestExists_thenCancel() {
      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, true))
          .thenReturn(Optional.of(testRequest));
      when(friendRequestDao.saveOrUpdate(any(FriendRequest.class)))
          .thenAnswer(invocation -> {
            FriendRequest r = invocation.getArgument(0);
            r.setStatus(FriendStatus.CANCELLED);
            return r;
          });

      FriendRequest result = friendRequestService.cancel(testUser1, testUser2);

      assertThat(result.getStatus()).isEqualTo(FriendStatus.CANCELLED);
      verify(friendRequestDao).saveOrUpdate(testRequest);
    }

    @Test
    void cancel_whenRequestNotExists_thenThrowException() {
      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, true))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> friendRequestService.cancel(testUser1, testUser2))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Заявка в друзья не найдена");
    }
  }

  @Nested
  class ReplyToRequestTests {
    @Test
    void replyToRequest_whenValidRequest_thenUpdateStatus() {
      when(friendRequestDao.getByUsersIds(2L, TEST_USER_ID, true))
          .thenReturn(Optional.of(testRequest));
      when(friendRequestDao.saveOrUpdate(any(FriendRequest.class)))
          .thenAnswer(invocation -> {
            FriendRequest r = invocation.getArgument(0);
            r.setStatus(FriendStatus.ACCEPTED);
            return r;
          });

      FriendRequest result = friendRequestService.replyToRequest(
          testUser2, testUser1, FriendStatus.ACCEPTED);

      assertThat(result.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
      verify(friendRequestDao).saveOrUpdate(testRequest);
    }

    @Test
    void replyToRequest_whenRequestNotExists_thenThrowException() {
      when(friendRequestDao.getByUsersIds(2L, TEST_USER_ID, true))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> friendRequestService.replyToRequest(
          testUser2, testUser1, FriendStatus.ACCEPTED))
          .isInstanceOf(FriendRequestException.class)
          .hasMessageContaining("нет активных запросов");
    }

    @Test
    void replyToRequest_whenAlreadyAccepted_thenThrowException() {
      FriendRequest acceptedRequest = FriendRequest.builder()
          .sender(testUser2)
          .recipient(testUser1)
          .status(FriendStatus.ACCEPTED)
          .build();

      when(friendRequestDao.getByUsersIds(2L, TEST_USER_ID, true))
          .thenReturn(Optional.of(acceptedRequest));

      assertThatThrownBy(() -> friendRequestService.replyToRequest(
          testUser2, testUser1, FriendStatus.ACCEPTED))
          .isInstanceOf(AlreadyFriendsException.class);
    }
  }

  @Nested
  class UnfriendTests {
    @Test
    void unfriend_whenFriends_thenCancelFriendship() {
      FriendRequest friendship = FriendRequest.builder()
          .sender(testUser1)
          .recipient(testUser2)
          .status(FriendStatus.ACCEPTED)
          .build();

      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, false))
          .thenReturn(Optional.of(friendship));
      when(friendRequestDao.saveOrUpdate(any(FriendRequest.class)))
          .thenAnswer(invocation -> {
            FriendRequest r = invocation.getArgument(0);
            r.setStatus(FriendStatus.CANCELLED);
            return r;
          });

      FriendRequest result = friendRequestService.unfriend(testUser1, testUser2);

      assertThat(result.getStatus()).isEqualTo(FriendStatus.CANCELLED);
      verify(friendRequestDao).saveOrUpdate(friendship);
    }

    @Test
    void unfriend_whenNotFriends_thenThrowException() {
      when(friendRequestDao.getByUsersIds(TEST_USER_ID, 2L, false))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> friendRequestService.unfriend(testUser1, testUser2))
          .isInstanceOf(FriendRequestException.class)
          .hasMessageContaining("не является другом");
    }
  }

  @Nested
  class IsFriendsTests {
    @Test
    void isFriends_whenFriends_thenReturnTrue() {
      when(friendRequestDao.areFriends(TEST_USER_ID, 2L)).thenReturn(true);

      boolean result = friendRequestService.isFriends(TEST_USER_ID, 2L);

      assertThat(result).isTrue();
      verify(friendRequestDao).areFriends(TEST_USER_ID, 2L);
    }

    @Test
    void isFriends_whenNotFriends_thenReturnFalse() {
      when(friendRequestDao.areFriends(TEST_USER_ID, 2L)).thenReturn(false);

      boolean result = friendRequestService.isFriends(TEST_USER_ID, 2L);

      assertThat(result).isFalse();
      verify(friendRequestDao).areFriends(TEST_USER_ID, 2L);
    }
  }
}