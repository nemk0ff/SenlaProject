package ru.senla.socialnetwork.model.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
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
import ru.senla.socialnetwork.model.MyEntity;

@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQuery(name = "Chat.find",
    query = "SELECT c FROM Chat c JOIN FETCH c.members WHERE c.id = :id")
@NamedQuery(name = "Chat.existsByMembers",
    query = "SELECT COUNT(cm1.chat.id) FROM ChatMember cm1 " +
        "JOIN ChatMember cm2 ON cm1.chat.id = cm2.chat.id " +
        "WHERE cm1.user.email = :email1 AND cm2.user.email = :email2 " +
        "AND cm1.chat.isGroup = false")
@NamedQuery(name = "Chat.findWithMembersAndUsers",
    query = "SELECT c FROM Chat c JOIN FETCH c.members m JOIN FETCH m.user WHERE c.id = :chatId")
@NamedQuery(name = "Chat.findAllChatsByUserId",
    query = "SELECT c FROM Chat c WHERE EXISTS (SELECT 1 FROM ChatMember cm " +
        "WHERE cm.chat.id = c.id AND cm.user.id = :userId " +
        "AND (cm.leaveDate IS NULL OR cm.joinDate > cm.leaveDate)) " +
        "ORDER BY c.createdAt DESC")
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime createdAt;

  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Message> messages = new ArrayList<>();

  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ChatMember> members;
}
