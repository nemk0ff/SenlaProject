package ru.senla.socialnetwork.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadyFriendsException;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadySentException;
import ru.senla.socialnetwork.exceptions.friendRequests.FriendRequestException;
import ru.senla.socialnetwork.exceptions.friendRequests.SelfFriendshipException;
import ru.senla.socialnetwork.exceptions.users.UserNotRegisteredException;
import ru.senla.socialnetwork.model.entities.FriendRequest;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.model.enums.FriendStatus;
import ru.senla.socialnetwork.repository.UserDao;
import ru.senla.socialnetwork.repository.impl.FriendRequestDaoImpl;
import ru.senla.socialnetwork.services.FriendRequestService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
  private final UserDao userDao;
  private final FriendRequestDaoImpl friendRequestDao;

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getAllByUser(String userEmail) {
    User user = getUser(userEmail);
    return friendRequestDao.getAllByUserId(user.getId());
  }

  @Transactional(readOnly = true)
  @Override
  public List<User> getFriendsByUser(String userEmail) {
    User user = getUser(userEmail);
    return friendRequestDao.findFriendsByUserId(user.getId());
  }

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getIncomingRequests(String userEmail, FriendStatus status) {
    return getAllByUser(userEmail)
        .stream()
        .filter(request -> request.getRecipient().getEmail().equals(userEmail)
            && request.getStatus().equals(status))
        .toList();
  }

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getOutgoingRequests(String userEmail) {
    return getAllByUser(userEmail)
        .stream()
        .filter(request -> request.getSender().getEmail().equals(userEmail)
            && !request.getStatus().equals(FriendStatus.ACCEPTED))
        .toList();
  }

  @Transactional
  @Override
  public FriendRequest sendRequest(String senderEmail, String recipientEmail) {
    if (senderEmail.equals(recipientEmail)) {
      throw new SelfFriendshipException();
    }
    User sender = getUser(senderEmail);
    User recipient = getUser(recipientEmail);

    Optional<FriendRequest> optionalRequest = friendRequestDao.getByUsersIds(sender.getId(),
        recipient.getId(), false);
    if (optionalRequest.isEmpty()) {
      return friendRequestDao.add(FriendRequest.builder()
          .sender(sender)
          .recipient(recipient)
          .status(FriendStatus.PENDING)
          .createdAt(LocalDateTime.now())
          .build());
    }

    FriendRequest request = handleExistingRequest(optionalRequest.get(),
        senderEmail, recipientEmail);
    return friendRequestDao.update(request);
  }

  private FriendRequest handleExistingRequest(FriendRequest request, String senderEmail,
                                              String recipientEmail) {
    if (request.getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new AlreadyFriendsException(recipientEmail);
    } else if (request.getSender().getEmail().equals(senderEmail)) {
      if (request.getStatus().equals(FriendStatus.PENDING)) {
        throw new AlreadySentException(recipientEmail);
      } else if (request.getStatus().equals(FriendStatus.REJECTED)) {
        request.setStatus(FriendStatus.PENDING);
      }
    } else if (request.getSender().getEmail().equals(recipientEmail)) {
      request.setStatus(FriendStatus.ACCEPTED);
    }
    return request;
  }

  @Transactional
  @Override
  public FriendRequest replyToRequest(String senderEmail, String recipientEmail, FriendStatus status) {
    if (status != FriendStatus.ACCEPTED && status != FriendStatus.REJECTED) {
      throw new IllegalArgumentException("Недопустимый статус для ответа: " + status);
    }
    User sender = getUser(senderEmail);
    User recipient = getUser(recipientEmail);

    Optional<FriendRequest> optionalRequest = friendRequestDao.getByUsersIds(sender.getId(),
        recipient.getId(), true);
    if (optionalRequest.isEmpty()) {
      throw new FriendRequestException(
          "У вас нет активных запросов на дружбу от " + senderEmail);
    } else if (optionalRequest.get().getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new AlreadyFriendsException(recipientEmail);
    }

    FriendRequest repliedRequest = optionalRequest.get();
    repliedRequest.setStatus(status);
    return friendRequestDao.update(repliedRequest);
  }

  @Transactional
  @Override
  public void unfriend(String userEmail, String unfriendEmail) {
    User user = getUser(userEmail);
    User unfriend = getUser(unfriendEmail);

    Optional<FriendRequest> friendship = friendRequestDao.getByUsersIds(
        user.getId(), unfriend.getId(), false);
    if (friendship.isEmpty() || !friendship.get().getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new FriendRequestException(unfriendEmail + " не является другом " + userEmail);
    }

    friendRequestDao.delete(friendship.get());
  }

  private User getUser(String email) {
    return userDao.findByEmail(email).orElseThrow(
        () -> new UserNotRegisteredException(email));
  }
}
