package ru.senla.socialnetwork.facades.friendRequests;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.mappers.FriendRequestMapper;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.dto.users.UserDTO;
import ru.senla.socialnetwork.exceptions.friendRequests.SelfFriendshipException;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FriendRequestFacadeImpl implements FriendRequestFacade {
  private final FriendRequestService friendRequestService;
  private final UserService userService;
  private final FriendRequestMapper friendRequestMapper;


  @Override
  public List<FriendRequestDTO> getAllByUser(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    return friendRequestMapper.toListDTO(friendRequestService.getAllByUser(user.getId()));
  }

  @Override
  public List<UserDTO> getFriendsByUser(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    return UserMapper.INSTANCE.toListUserResponseDTO(friendRequestService.getFriendsByUser(user.getId()));
  }

  @Override
  public List<FriendRequestDTO> getIncomingRequests(String userEmail, FriendStatus status) {
    User user = userService.getUserByEmail(userEmail);
    return friendRequestMapper.toListDTO(
        friendRequestService.getIncomingRequests(user.getId(), status));
  }

  @Override
  public List<FriendRequestDTO> getOutgoingRequests(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    return friendRequestMapper.toListDTO(
        friendRequestService.getOutgoingRequests(user.getId()));
  }

  @Override
  public FriendRequestDTO sendRequest(String senderEmail, String recipientEmail) {
    if (senderEmail.equals(recipientEmail)) {
      throw new SelfFriendshipException();
    }
    User sender = userService.getUserByEmail(senderEmail);
    User recipient = userService.getUserByEmail(recipientEmail);
    return friendRequestMapper.toDto(friendRequestService.sendRequest(sender, recipient));
  }

  @Override
  public FriendRequestDTO replyToRequest(String senderEmail, String recipientEmail, FriendStatus status) {
    if (status != FriendStatus.ACCEPTED && status != FriendStatus.REJECTED) {
      throw new IllegalArgumentException("Недопустимый статус для ответа: " + status);
    }
    User sender = userService.getUserByEmail(senderEmail);
    User recipient = userService.getUserByEmail(recipientEmail);

    return friendRequestMapper.toDto(friendRequestService.replyToRequest(sender, recipient, status));
  }

  @Override
  public void unfriend(String userEmail, String unfriendEmail) {
    User user = userService.getUserByEmail(userEmail);
    User unfriend = userService.getUserByEmail(unfriendEmail);
    friendRequestService.unfriend(user, unfriend);
  }
}
