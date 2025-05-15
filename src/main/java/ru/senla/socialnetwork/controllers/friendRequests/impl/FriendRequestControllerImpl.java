package ru.senla.socialnetwork.controllers.friendRequests.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.friendRequests.FriendRequestController;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.facades.friendRequests.FriendRequestFacade;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/friends")
public class FriendRequestControllerImpl implements FriendRequestController {
  private final FriendRequestFacade friendRequestFacade;

  @Override
  @GetMapping("/requests")
  @PreAuthorize("hasRole('ADMIN') or #userEmail == authentication.name")
  public ResponseEntity<?> showAllByUser(@RequestParam @Email String userEmail) {
    log.info("Запрос всех заявок пользователя: {}", userEmail);
    List<FriendRequestDTO> requests = friendRequestFacade.getAllByUser(userEmail);
    log.info("Найдено {} заявок у пользователя {}", requests.size(), userEmail);
    return ResponseEntity.ok(requests);
  }

  @Override
  @GetMapping
  public ResponseEntity<?> showFriends(@RequestParam @Email String userEmail) {
    log.info("Запрос списка друзей пользователя: {}", userEmail);
    List<UserResponseDTO> friends = friendRequestFacade.getFriendsByUser(userEmail);
    log.info("Найдено {} друзей для пользователя {}", friends.size(), userEmail);
    return ResponseEntity.ok(friends);
  }

  @Override
  @GetMapping("/outgoing")
  public ResponseEntity<?> showOutgoingRequests(Authentication auth) {
    log.info("Запрос исходящих заявок пользователя: {}", auth.getName());
    List<FriendRequestDTO> requests = friendRequestFacade.getOutgoingRequests(auth.getName());
    log.info("Найдено {} исходящих заявок для пользователя {}", requests.size(), auth.getName());
    return ResponseEntity.ok(requests);
  }

  @Override
  @GetMapping("/incoming")
  public ResponseEntity<?> showIncomingRequests(
      @RequestParam FriendStatus status,
      Authentication auth) {
    log.info("Запрос входящих заявок для {} со статусом {}", auth.getName(), status);
    List<FriendRequestDTO> requests = friendRequestFacade.getIncomingRequests(auth.getName(), status);
    log.info("Найдено {} входящих заявок для {} со статусом {}", requests.size(), auth.getName(), status);
    return ResponseEntity.ok(requests);
  }

  @Override
  @PostMapping("/request")
  public ResponseEntity<?> sendRequest(
      @RequestParam @Email String recipient,
      Authentication auth) {
    log.info("Попытка отправить заявку от {} к {}", auth.getName(), recipient);
    FriendRequestDTO response = friendRequestFacade.send(auth.getName(), recipient);
    log.info("Заявка успешно отправлена от {} к {}. ID заявки: {}",
        auth.getName(), recipient, response.id());
    return ResponseEntity.ok(response);
  }

  @Override
  @DeleteMapping("/request")
  public ResponseEntity<?> cancelRequest(
      @RequestParam @Email String recipient,
      Authentication auth) {
    log.info("Попытка отменить заявку от {} к {}", auth.getName(), recipient);
    FriendRequestDTO response = friendRequestFacade.cancel(auth.getName(), recipient);
    log.info("Заявка успешно отменена от {} к {}. ID заявки: {}",
        auth.getName(), recipient, response.id());
    return ResponseEntity.ok(response);
  }

  @Override
  @PatchMapping("/respond")
  public ResponseEntity<?> respondRequest(
      @RequestBody @Valid RespondRequestDTO request,
      Authentication auth) {
    log.info("Попытка обработки заявки от {} к {}. Новый статус: {}",
        request.senderEmail(), auth.getName(), request.respondStatus());
    FriendRequestDTO response = friendRequestFacade.respond(request, auth.getName());
    log.info("Заявка от {} к {} обновлена. Статус: {}",
        request.senderEmail(), auth.getName(), response.status());
    return ResponseEntity.ok(response);
  }

  @Override
  @DeleteMapping("/remove")
  public ResponseEntity<?> removeFriend(
      @RequestParam @Email String recipient,
      Authentication auth) {
    log.info("Попытка удаления из друзей: {} удаляет {}", auth.getName(), recipient);
    FriendRequestDTO friendRequestDTO = friendRequestFacade.unfriend(auth.getName(), recipient);
    log.info("Пользователь {} успешно удален из друзей {}", recipient, auth.getName());
    return ResponseEntity.ok(friendRequestDTO);
  }
}
