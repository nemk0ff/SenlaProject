package ru.senla.socialnetwork.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.senla.socialnetwork.model.general.Post;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("WALL")
public final class WallPost extends Post {
  @ManyToOne
  @JoinColumn(name = "wall_owner_id", nullable = false)
  private User wall_owner;

  @Column(name = "mood", length = 32)
  private String mood;

  @Column(name = "location")
  private String location;

  @Override
  public String getPostType() {
    return "WallPost";
  }
}