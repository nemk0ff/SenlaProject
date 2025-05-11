package ru.senla.socialnetwork.services.friendRequest;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.friendRequests.FriendRequestDao;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadyFriendsException;
import ru.senla.socialnetwork.exceptions.friendRequests.AlreadySentException;
import ru.senla.socialnetwork.exceptions.friendRequests.FriendRequestException;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@Slf4j
@Service
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
  private final FriendRequestDao friendRequestDao;

  @Override
  public List<FriendRequest> getAllByUser(Long userId) {
    return friendRequestDao.getAllByUserId(userId);
  }

  @Override
  public List<User> getFriendsByUser(Long userId) {
    return friendRequestDao.findFriendsByUserId(userId);
  }

  @Override
  public List<FriendRequest> getIncomingRequests(Long userId, FriendStatus status) {
    return getAllByUser(userId)
        .stream()
        .filter(request -> request.getRecipient().getId().equals(userId)
            && request.getStatus().equals(status))
        .toList();
  }

  @Override
  public List<FriendRequest> getOutgoingRequests(Long userId) {
    return getAllByUser(userId)
        .stream()
        .filter(request -> request.getSender().getId().equals(userId)
            && !request.getStatus().equals(FriendStatus.ACCEPTED))
        .toList();
  }

  @Override
  public FriendRequest send(User sender, User recipient) {
    log.info("Попытка отправить заявку в друзья от {} к {}...",
        sender.getEmail(), recipient.getEmail());
    Optional<FriendRequest> optionalRequest = friendRequestDao.getByUsersIds(sender.getId(),
        recipient.getId(), false);
    if (optionalRequest.isEmpty()) {
      log.info("Заявок между {} и {} не найдено, создаём новую...",
          sender.getEmail(), recipient.getEmail());
      return friendRequestDao.saveOrUpdate(FriendRequest.builder()
          .sender(sender)
          .recipient(recipient)
          .status(FriendStatus.PENDING)
          .createdAt(ZonedDateTime.now())
          .build());
    }
    log.info("Заявка между {} и {} уже существует, проверяем наличие несоответствий...",
        sender.getEmail(), recipient.getEmail());
    FriendRequest request = handleExistingRequest(optionalRequest.get(),
        sender.getEmail(), recipient.getEmail());
    return friendRequestDao.saveOrUpdate(request);
  }

  @Override
  public FriendRequest cancel(User sender, User recipient) {
    FriendRequest request = friendRequestDao
        .getByUsersIds(sender.getId(), recipient.getId(), true)
        .orElseThrow(() -> new EntityNotFoundException("Заявка в друзья не найдена"));
    request.setStatus(FriendStatus.CANCELLED);
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
        log.info("Возобновление отклонённого запроса в друзья от {} к {}", senderEmail, recipientEmail);
        request.setStatus(FriendStatus.PENDING);
      } else if (request.getStatus().equals(FriendStatus.CANCELLED)) {
        log.info("Возобновление отменённого запроса в друзья от {} к {}", senderEmail, recipientEmail);
        request.setStatus(FriendStatus.PENDING);
      }
    } else if (request.getSender().getEmail().equals(recipientEmail)) {
      log.info("Автопринятие взаимного запроса в друзья от {} к {}", senderEmail, recipientEmail);
      request.setStatus(FriendStatus.ACCEPTED);
    }
    return request;
  }

  @Override
  public FriendRequest replyToRequest(User sender, User recipient, FriendStatus status) {
    Optional<FriendRequest> optionalRequest = friendRequestDao.getByUsersIds(sender.getId(),
        recipient.getId(), true);
    if (optionalRequest.isEmpty()
        || optionalRequest.get().getStatus().equals(FriendStatus.CANCELLED)) {
      throw new FriendRequestException(
          "У вас нет активных запросов на дружбу от " + sender.getEmail());
    } else if (optionalRequest.get().getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new AlreadyFriendsException(recipient.getEmail());
    }

    FriendRequest repliedRequest = optionalRequest.get();
    repliedRequest.setStatus(status);
    log.info("Установлен статус {} для заявки {}:", status, repliedRequest.getId());
    return friendRequestDao.saveOrUpdate(repliedRequest);
  }

  @Override
  public FriendRequest unfriend(User user, User unfriend) {
    Optional<FriendRequest> friendship = friendRequestDao.getByUsersIds(
        user.getId(), unfriend.getId(), false);
    if (friendship.isEmpty() || !friendship.get().getStatus().equals(FriendStatus.ACCEPTED)) {
      throw new FriendRequestException(unfriend.getEmail() + " не является другом " + user.getEmail());
    }
    FriendRequest request = friendship.get();
    request.setStatus(FriendStatus.CANCELLED);
    return friendRequestDao.saveOrUpdate(request);
  }

  @Override
  public boolean isFriends(Long firstId, Long secondId) {
    return friendRequestDao.areFriends(firstId, secondId);
  }
}
