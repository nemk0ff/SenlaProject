package ru.senla.socialnetwork.model.chats;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.senla.socialnetwork.model.general.MyEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chats")
public final class Chat implements MyEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "is_group", nullable = false)
  private Boolean isGroup;

  @Column(name = "created_at")
  private ZonedDateTime createdAt;
//
//  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
//  private List<ChatMessage> messages = new ArrayList<>();

  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
  private List<ChatMember> members = new ArrayList<>();
}
