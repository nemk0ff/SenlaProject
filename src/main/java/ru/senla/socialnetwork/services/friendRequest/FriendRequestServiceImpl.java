package ru.senla.socialnetwork.services.friendRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.friendRequests.FriendRequestDao;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadyFriendsException;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadySentException;
import ru.senla.socialnetwork.exceptions.friendRequests.FriendRequestException;
import ru.senla.socialnetwork.exceptions.friendRequests.SelfFriendshipException;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
  private final FriendRequestDao friendRequestDao;

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getAllByUser(Long userId) {
    return friendRequestDao.getAllByUserId(userId);
  }

  @Transactional(readOnly = true)
  @Override
  public List<User> getFriendsByUser(Long userId) {
    return friendRequestDao.findFriendsByUserId(userId);
  }

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getIncomingRequests(Long userId, FriendStatus status) {
    return getAllByUser(userId)
        .stream()
        .filter(request -> request.getRecipient().getId().equals(userId)
            && request.getStatus().equals(status))
        .toList();
  }

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getOutgoingRequests(Long userId) {
    return getAllByUser(userId)
        .stream()
        .filter(request -> request.getSender().getId().equals(userId)
            && !request.getStatus().equals(FriendStatus.ACCEPTED))
        .toList();
  }

  @Transactional
  @Override
  public FriendRequest sendRequest(User sender, User recipient) {
    Optional<FriendRequest> optionalRequest = friendRequestDao.getByUsersIds(sender.getId(),
        recipient.getId(), false);
    if (optionalRequest.isEmpty()) {
      return friendRequestDao.saveOrUpdate(FriendRequest.builder()
          .sender(sender)
          .recipient(recipient)
          .status(FriendStatus.PENDING)
          .createdAt(LocalDateTime.now())
          .build());
    }
    FriendRequest request = handleExistingRequest(optionalRequest.get(),
        sender.getEmail(), recipient.getEmail());
    return friendRequestDao.saveOrUpdate(request);
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
  public FriendRequest replyToRequest(User sender, User recipient, FriendStatus status) {
    Optional<FriendRequest> optionalRequest = friendRequestDao.getByUsersIds(sender.getId(),
        recipient.getId(), true);
    if (optionalRequest.isEmpty()) {
      throw new FriendRequestException(
          "У вас нет активных запросов на дружбу от " + sender.getEmail());
    } else if (optionalRequest.get().getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new AlreadyFriendsException(recipient.getEmail());
    }

    FriendRequest repliedRequest = optionalRequest.get();
    repliedRequest.setStatus(status);
    return friendRequestDao.saveOrUpdate(repliedRequest);
  }

  @Transactional
  @Override
  public void unfriend(User user, User unfriend) {
    Optional<FriendRequest> friendship = friendRequestDao.getByUsersIds(
        user.getId(), unfriend.getId(), false);
    if (friendship.isEmpty() || !friendship.get().getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new FriendRequestException(unfriend.getEmail() + " не является другом " + user.getEmail());
    }
    friendRequestDao.delete(friendship.get());
  }
}
