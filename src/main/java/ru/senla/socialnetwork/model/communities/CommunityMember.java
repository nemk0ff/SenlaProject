package ru.senla.socialnetwork.model.communities;

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
import ru.senla.socialnetwork.model.GroupMember;


@Entity
@DiscriminatorValue("COMMUNITY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@NamedQuery(name = "CommunityMember.findByCommunityIdAndUserEmail",
    query = "FROM CommunityMember cm JOIN FETCH cm.user JOIN FETCH cm.community " +
        "WHERE cm.community.id = :communityId AND lower(cm.user.email) = lower(:userEmail)")
@NamedQuery(name = "CommunityMember.findAllByCommunityId",
    query = "FROM CommunityMember cm JOIN FETCH cm.user JOIN FETCH cm.community " +
        "WHERE cm.community.id = :communityId " +
        "AND (cm.leaveDate IS NULL OR cm.joinDate > cm.leaveDate)")
@NamedQuery(name = "CommunityMember.findAllByUserId",
    query = "SELECT c FROM CommunityMember c JOIN FETCH c.user JOIN FETCH c.community " +
        "JOIN FETCH c.community WHERE c.user.id = :userId")
public final class CommunityMember extends GroupMember {
  @ManyToOne
  @JoinColumn(name = "community_id")
  private Community community;

  @Column(name = "is_banned")
  private Boolean isBanned;

  @Column(name = "banned_reason")
  private String bannedReason;
}
