package ru.senla.socialnetwork.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.senla.socialnetwork.model.users.User;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "gm_type", discriminatorType = DiscriminatorType.STRING)
@Entity
@Table(name = "group_members")
public abstract class GroupMember implements MyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  protected Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  protected User user;

  @Column(name = "join_date", nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  protected ZonedDateTime joinDate;

  @Column(name = "leave_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  protected ZonedDateTime leaveDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  protected MemberRole role;

  public boolean isUserInGroup() {
    // В чате, если дата входа позже даты выхода
    // Или если выход не указан (никогда не выходил)
    return leaveDate == null || joinDate.isAfter(leaveDate);
  }
}

