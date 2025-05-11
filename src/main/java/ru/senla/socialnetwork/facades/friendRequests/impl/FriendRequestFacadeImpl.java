package ru.senla.socialnetwork.facades.friendRequests.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.mappers.FriendRequestMapper;
import ru.senla.socialnetwork.dto.mappers.UserMapper;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.exceptions.friendRequests.SelfFriendshipException;
import ru.senla.socialnetwork.facades.friendRequests.FriendRequestFacade;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.user.UserService;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
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
  public List<UserResponseDTO> getFriendsByUser(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    return UserMapper.INSTANCE.toListUserResponseDTO(friendRequestService.getFriendsByUser(user.getId()));
  }

  @Override
  public List<FriendRequestDTO> getIncomingRequests(String userEmail, FriendStatus status) {
    User user = userService.getUserByEmail(userEmail);
    return friendRequestMapper.toListDTO(friendRequestService.getIncomingRequests(user.getId(), status));
  }

  @Override
  public List<FriendRequestDTO> getOutgoingRequests(String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    return friendRequestMapper.toListDTO(friendRequestService.getOutgoingRequests(user.getId()));
  }

  @Override
  public FriendRequestDTO send(String senderEmail, String recipientEmail) {
    if (senderEmail.equals(recipientEmail)) {
      throw new SelfFriendshipException();
    }
    User sender = userService.getUserByEmail(senderEmail);
    User recipient = userService.getUserByEmail(recipientEmail);
    return friendRequestMapper.toDTO(friendRequestService.send(sender, recipient));
  }

  @Override
  public FriendRequestDTO cancel(String senderEmail, String recipientEmail) {
    User sender = userService.getUserByEmail(senderEmail);
    User recipient = userService.getUserByEmail(recipientEmail);
    return friendRequestMapper.toDTO(friendRequestService.cancel(sender, recipient));
  }

  @Override
  public FriendRequestDTO respond(RespondRequestDTO requestDTO, String recipientEmail) {
    if (requestDTO.respondStatus().equals(FriendStatus.PENDING)) {
      throw new IllegalArgumentException("Недопустимый статус для ответа: " + requestDTO.respondStatus());
    }
    User sender = userService.getUserByEmail(requestDTO.senderEmail());
    User recipient = userService.getUserByEmail(recipientEmail);

    return friendRequestMapper.toDTO(friendRequestService.replyToRequest(sender, recipient, requestDTO.respondStatus()));
  }

  @Override
  public FriendRequestDTO unfriend(String userEmail, String unfriendEmail) {
    User user = userService.getUserByEmail(userEmail);
    User unfriend = userService.getUserByEmail(unfriendEmail);
    return friendRequestMapper.toDTO(friendRequestService.unfriend(user, unfriend));
  }
}
