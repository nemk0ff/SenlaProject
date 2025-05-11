package ru.senla.socialnetwork.model.users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.senla.socialnetwork.model.MyEntity;
import ru.senla.socialnetwork.model.chats.ChatMember;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public final class User implements MyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, columnDefinition = "user_role")
  private UserRole role;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "name", nullable = false, length = 32)
  private String name;

  @Column(name = "surname", nullable = false, length = 32)
  private String surname;

  @Column(name = "birthdate")
  private LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", columnDefinition = "gender_type")
  private Gender gender;

  @Enumerated(EnumType.STRING)
  @Column(name = "profile_type", columnDefinition = "profile_type")
  private ProfileType profileType;

  @Column(name = "about_me", length = 1000)
  private String aboutMe;

  @Column(name = "registered_at")
  private ZonedDateTime registeredAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<ChatMember> chatMemberships = new ArrayList<>();
}
