package ru.senla.socialnetwork.model.communities;

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
import ru.senla.socialnetwork.model.GroupMember;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("COMMUNITY")
public final class CommunityMember extends GroupMember {
  @ManyToOne
  @JoinColumn(name = "community_id")
  private Community community;

  @Column(name = "is_banned")
  private Boolean isBanned = false;

  @Column(name = "banned_reason")
  private String bannedReason;
}
