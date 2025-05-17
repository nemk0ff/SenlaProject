package ru.senla.socialnetwork.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.senla.socialnetwork.model.Post;

@Entity
@DiscriminatorValue("WALL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@NamedQuery(name = "Wall.findAllByUserId",
    query = "FROM WallPost wp JOIN FETCH wp.wallOwner WHERE wp.wallOwner.id = :userId")
public final class WallPost extends Post {
  @ManyToOne
  @JoinColumn(name = "wall_owner_id", nullable = false)
  private User wallOwner;

  @Column(name = "mood", length = 32)
  private String mood;

  @Column(name = "location")
  private String location;

  @Override
  public String getPostType() {
    return "WallPost";
  }
}