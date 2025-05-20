package ru.senla.socialnetwork.controllers.friendRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Friend Requests", description = "API для управления друзьями и заявками в друзья")
public interface FriendRequestController {

  @Operation(summary = "Получить все заявки пользователя",
      description = "Доступно только администраторам или самому пользователю")
  @ApiResponse(responseCode = "200", description = "Список всех заявок пользователя",
      content = @Content(schema = @Schema(implementation = FriendRequestDTO[].class)))
  ResponseEntity<?> showAllByUser(@Email String userEmail);

  @Operation(summary = "Получить список друзей пользователя")
  @ApiResponse(responseCode = "200", description = "Список друзей пользователя",
      content = @Content(schema = @Schema(implementation = UserResponseDTO[].class)))
  ResponseEntity<?> showFriends(@Email String userEmail);

  @Operation(summary = "Получить исходящие заявки",
      description = "Возвращает все заявки, отправленные текущим пользователем")
  @ApiResponse(responseCode = "200", description = "Список исходящих заявок",
      content = @Content(schema = @Schema(implementation = FriendRequestDTO[].class)))
  ResponseEntity<?> showOutgoingRequests(Authentication auth);

  @Operation(summary = "Получить входящие заявки",
      description = "Возвращает заявки, полученные текущим пользователем, с указанным статусом")
  @ApiResponse(responseCode = "200", description = "Список входящих заявок",
      content = @Content(schema = @Schema(implementation = FriendRequestDTO[].class)))
  ResponseEntity<?> showIncomingRequests(FriendStatus status, Authentication auth);

  @Operation(summary = "Отправить заявку в друзья")
  @ApiResponse(responseCode = "200", description = "Заявка успешно отправлена",
      content = @Content(schema = @Schema(implementation = FriendRequestDTO.class)))
  ResponseEntity<?> sendRequest(@Email String recipient, Authentication auth);

  @Operation(summary = "Отменить заявку в друзья")
  @ApiResponse(responseCode = "200", description = "Заявка успешно отменена",
      content = @Content(schema = @Schema(implementation = FriendRequestDTO.class)))
  ResponseEntity<?> cancelRequest(@Email String recipient, Authentication auth);

  @Operation(summary = "Ответить на заявку в друзья",
      description = "Подтвердить или отклонить входящую заявку")
  @ApiResponse(responseCode = "200", description = "Заявка успешно обработана",
      content = @Content(schema = @Schema(implementation = FriendRequestDTO.class)))
  ResponseEntity<?> respondRequest(@Valid RespondRequestDTO request, Authentication auth);

  @Operation(summary = "Удалить из друзей")
  @ApiResponse(responseCode = "200", description = "Пользователь успешно удален из друзей",
      content = @Content(schema = @Schema(implementation = FriendRequestDTO.class)))
  ResponseEntity<?> removeFriend(@Email String recipient, Authentication auth);
}
