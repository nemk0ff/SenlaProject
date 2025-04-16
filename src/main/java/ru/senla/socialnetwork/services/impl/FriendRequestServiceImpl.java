package ru.senla.socialnetwork.services.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.exceptions.FriendRequestException;
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
    User user = userDao.findByEmail(userEmail).orElseThrow(() ->
        new EntityNotFoundException("Пользователь " + userEmail + " не зарегистрирован."));
    return friendRequestDao.getAllByUserId(user.getId());
  }

  @Transactional(readOnly = true)
  @Override
  public List<User> getFriendsByUser(String userEmail) {
    List<FriendRequest> acceptedRequests = getAllByUser(userEmail)
        .stream()
        .filter(request -> request.getStatus().equals(FriendStatus.ACCEPTED))
        .toList();
    List<User> friends = new ArrayList<>();
    for (FriendRequest r : acceptedRequests) {
      String friendEmail = !r.getSender().getEmail().equals(userEmail)
          ? r.getSender().getEmail() : r.getRecipient().getEmail();
      friends.add(userDao.findByEmail(friendEmail)
          .orElseThrow(() -> new EntityNotFoundException("Пользователь не зарегистрирован")));
    }
    return friends;
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
    // Если sender == recipient - IllegalArg
    // Если sender или recipient не существуют - IllegalArg
    // Если заявка уже есть в статусе Accepted, то нужно вернуть ошибку AlreadyExists
    // Если есть заявка от recipient в статусе Pending или Canceled, то нужно принять её
    // Если есть заявка от sender в статусе Pending, то нужно вернуть ошибку AlreadyExists
    // Если есть заявка от sender в статусе Rejected, то нужно переотправить запрос на дружбу
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

    FriendRequest request = optionalRequest.get();
    if (request.getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new FriendRequestException(recipientEmail + " уже ваш друг");
    } else if (request.getSender().equals(sender)) {
      if (request.getStatus().equals(FriendStatus.PENDING)) {
        throw new FriendRequestException(
            "Вы уже отправили заявку в друзья пользователю " + recipientEmail);
      } else if (request.getStatus().equals(FriendStatus.REJECTED)) {
        request.setStatus(FriendStatus.PENDING);
      }
    } else if (request.getSender().equals(recipient)) {
      request.setStatus(FriendStatus.ACCEPTED);
    }
    return friendRequestDao.update(request);
  }

  @Transactional
  @Override
  public FriendRequest replyToRequest(String senderEmail, String recipientEmail, FriendStatus status) {
    User sender = getUser(senderEmail);
    User recipient = getUser(recipientEmail);

    Optional<FriendRequest> optionalRequest = friendRequestDao.getByUsersIds(sender.getId(),
        recipient.getId(), true);

    if (optionalRequest.isEmpty()
        || optionalRequest.get().getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new FriendRequestException("У вас нет активных запросов на дружбу от " + senderEmail);
    } else {
      FriendRequest acceptedRequest = optionalRequest.get();
      acceptedRequest.setStatus(FriendStatus.ACCEPTED);
      return friendRequestDao.update(acceptedRequest);
    }
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
        () -> new FriendRequestException("Пользователь не зарегистрирован: " + email));
  }
}
