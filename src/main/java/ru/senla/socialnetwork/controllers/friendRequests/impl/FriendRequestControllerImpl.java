package ru.senla.socialnetwork.controllers.friendRequests.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.friendRequests.FriendRequestController;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.SendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RemoveFriendRequestDTO;
import ru.senla.socialnetwork.dto.users.UserDTO;
import ru.senla.socialnetwork.facades.friendRequests.FriendRequestFacade;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/friends")
public class FriendRequestControllerImpl implements FriendRequestController {
  private final FriendRequestFacade friendRequestFacade;

  @Override
  @GetMapping("/requests")
  @PreAuthorize("hasRole('ADMIN') or #userEmail == authentication.name")
  public ResponseEntity<?> showAllByUser(String userEmail) {
    log.info("Запрос всех заявок пользователя: {}", userEmail);
    List<FriendRequestDTO> requests = friendRequestFacade.getAllByUser(userEmail);
    log.info("Найдено {} заявок у пользователя {}", requests.size(), userEmail);
    return ResponseEntity.ok(requests);
  }

  @Override
  @GetMapping
  public ResponseEntity<?> showFriends(String userEmail) {
    log.info("Запрос списка друзей пользователя: {}", userEmail);
    List<UserDTO> friends = friendRequestFacade.getFriendsByUser(userEmail);
    log.info("Найдено {} друзей для пользователя {}", friends.size(), userEmail);
    return ResponseEntity.ok(friends);
  }

  @Override
  @GetMapping("/outgoing_requests")
  @PreAuthorize("hasRole('ADMIN') or #userEmail == authentication.name")
  public ResponseEntity<?> showOutgoingRequests(String userEmail) {
    log.info("Запрос исходящих заявок пользователя: {}", userEmail);
    List<FriendRequestDTO> requests = friendRequestFacade.getOutgoingRequests(userEmail);
    log.info("Найдено {} исходящих заявок для пользователя {}", requests.size(), userEmail);
    return ResponseEntity.ok(requests);
  }

  @Override
  @GetMapping("/incoming_requests")
  @PreAuthorize("hasRole('ADMIN') or #recipientEmail == authentication.name")
  public ResponseEntity<?> showIncomingRequests(
      @RequestParam @Email String recipientEmail,
      @RequestParam @NotNull FriendStatus status) {
    log.info("Запрос входящих заявок для {} со статусом {}", recipientEmail, status);
    List<FriendRequestDTO> requests = friendRequestFacade.getIncomingRequests(recipientEmail, status);
    log.info("Найдено {} входящих заявок для {} со статусом {}", requests.size(), recipientEmail, status);
    return ResponseEntity.ok(requests);
  }

  @Override
  @PostMapping("/send")
  @PreAuthorize("hasRole('ADMIN') or #request.senderEmail == authentication.name")
  public ResponseEntity<?> sendRequest(@RequestBody @Valid SendRequestDTO request) {
    log.info("Попытка отправить заявку от {} к {}", request.senderEmail(), request.recipientEmail());
    FriendRequestDTO response = friendRequestFacade.sendRequest(request.senderEmail(),
        request.recipientEmail());
    log.info("Заявка успешно отправлена от {} к {}. ID заявки: {}",
        request.senderEmail(), request.recipientEmail(), response.id());
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping("/respond")
  @PreAuthorize("hasRole('ADMIN') or #request.recipientEmail == authentication.name")
  public ResponseEntity<?> respondRequest(@RequestBody @Valid RespondRequestDTO request) {
    log.info("Попытка обработки заявки от {} к {}. Новый статус: {}",
        request.senderEmail(), request.recipientEmail(), request.status());
    FriendRequestDTO response = friendRequestFacade.replyToRequest(request.senderEmail(),
        request.recipientEmail(), request.status());
    log.info("Заявка от {} к {} обновлена. Статус: {}",
        request.senderEmail(), request.recipientEmail(), response.status());
    return ResponseEntity.ok(response);
  }

  @Override
  @DeleteMapping("/remove")
  @PreAuthorize("hasRole('ADMIN') or #request.userEmail == authentication.name")
  public ResponseEntity<?> removeFriend(@RequestBody @Valid RemoveFriendRequestDTO request) {
    log.info("Попытка удаления из друзей: {} удаляет {}", request.userEmail(), request.friendEmail());
    friendRequestFacade.unfriend(request.userEmail(), request.friendEmail());
    log.info("Пользователь {} успешно удален из друзей {}", request.friendEmail(), request.userEmail());
    return ResponseEntity.ok(request.friendEmail() + " удалён из списка друзей " + request.userEmail());
  }
}
