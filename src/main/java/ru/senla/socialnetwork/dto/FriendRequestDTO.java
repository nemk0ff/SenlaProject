package ru.senla.socialnetwork.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.senla.socialnetwork.model.enums.FriendStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FriendRequestDTO {
  private String senderEmail;
  private String recipientEmail;
  private LocalDateTime createdAt;
  private FriendStatus status;
}
