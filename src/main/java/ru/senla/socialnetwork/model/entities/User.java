package ru.senla.socialnetwork.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.senla.socialnetwork.model.enums.Gender;
import ru.senla.socialnetwork.model.enums.UserRole;

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

  @Column(name = "mail", nullable = false, unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, columnDefinition = "user_role")
  private UserRole role;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "firstname", nullable = false, length = 32)
  private String firstName;

  @Column(name = "lastname", nullable = false, length = 32)
  private String lastName;

  @Column(name = "birthdate")
  private LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", columnDefinition = "gender_type")
  private Gender gender;

  @Column(name = "about_me", length = 1000)
  private String aboutMe;

  @Column(name = "registered_at")
  private ZonedDateTime registeredAt;
}
