package ru.senla.socialnetwork.model.friendRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.senla.socialnetwork.model.MyEntity;
import ru.senla.socialnetwork.model.users.User;

@Entity
@Table(name = "friend_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQuery(name = "FriendRequest.findFriendsByUserId",
    query = "SELECT fr.recipient FROM FriendRequest fr " +
        "WHERE fr.sender.id = :userId AND fr.status = 'ACCEPTED' " +
        "UNION " +
        "SELECT fr.sender FROM FriendRequest fr " +
        "WHERE fr.recipient.id = :userId AND fr.status = 'ACCEPTED'")
@NamedQuery(name = "FriendRequest.getAllByUserId",
    query = "FROM FriendRequest f LEFT JOIN FETCH f.sender LEFT JOIN FETCH f.recipient " +
        "WHERE f.sender.id = :userId OR f.recipient.id = :userId")
@NamedQuery(name = "FriendRequest.areFriends",
    query = "SELECT COUNT(fr) > 0 FROM FriendRequest fr " +
        "WHERE ((fr.sender.id = :firstUser AND fr.recipient.id = :secondUser) " +
        "OR (fr.sender.id = :secondUser AND fr.recipient.id = :firstUser)) " +
        "AND fr.status = :status")
public final class FriendRequest implements MyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @ManyToOne
  @JoinColumn(name = "recipient_id", nullable = false)
  private User recipient;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private FriendStatus status;

  @Column(name = "created_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime createdAt;
}
