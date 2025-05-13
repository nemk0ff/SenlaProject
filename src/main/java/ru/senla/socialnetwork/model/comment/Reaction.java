package ru.senla.socialnetwork.model.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "reactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQuery(name = "Reaction.find",
    query = "SELECT r FROM Reaction r " +
        "JOIN FETCH r.owner JOIN FETCH r.comment" +
        " r.comment.id = :commentId")
@NamedQuery(name = "Reaction.findAll",
    query = "SELECT r FROM Reaction r " +
        "JOIN FETCH r.owner JOIN FETCH r.comment")
@NamedQuery(name = "Reaction.findAllByCommentId",
    query = "SELECT r FROM Reaction r " +
        "JOIN FETCH r.owner JOIN FETCH r.comment" +
        "WHERE r.comment.id = :commentId")
@NamedQuery(name = "Reaction.findByUserIdAndCommentId",
    query = "FROM Reaction " +
        "JOIN FETCH r.owner JOIN FETCH r.comment" +
        "WHERE comment.id = :commentId AND owner.id = :ownerId")
public final class Reaction implements MyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private ReactionType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User owner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  @Column(name = "created_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime createdAt;
}