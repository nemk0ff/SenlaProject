package ru.senla.socialnetwork.model.communities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@DiscriminatorValue("COMMUNITY")
public final class CommunityPost extends Post {
  @OneToOne
  @JoinColumn(name = "author_id", nullable = false)
  private CommunityMember author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_id", nullable = false)
  private Community community;

  @Column(name = "is_pinned")
  private Boolean isPinned;
}