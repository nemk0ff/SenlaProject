package ru.senla.socialnetwork.model.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.senla.socialnetwork.model.GroupMember;

@Entity
@DiscriminatorValue("CHAT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@NamedQuery(name = "ChatMember.findAllByChatId",
    query = "FROM ChatMember c JOIN FETCH c.user WHERE c.chat.id = :chatId")
@NamedQuery(name = "ChatMember.findByChatIdAndUserEmail",
    query = "FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.user.email = :email ")
@NamedQuery(name = "ChatMember.findActiveByChatIdAndUserEmail",
    query = "FROM ChatMember cm JOIN FETCH cm.user WHERE cm.chat.id = :chatId " +
        "AND cm.user.email = :email AND (cm.leaveDate IS NULL OR cm.joinDate > cm.leaveDate)")
@NamedQuery(name = "ChatMember.countByChatIdAndRole",
    query = "SELECT COUNT(cm) FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.role = :role " +
        "AND (cm.leaveDate IS NULL OR cm.joinDate > cm.leaveDate)")
@NamedQuery(name = "ChatMember.countByChatId",
    query = "SELECT COUNT(cm) FROM ChatMember cm WHERE cm.chat.id = :chatId " +
        "AND (cm.leaveDate IS NULL OR cm.joinDate > cm.leaveDate)")
public final class ChatMember extends GroupMember {
  @ManyToOne
  @JoinColumn(name = "chat_id")
  private Chat chat;

  @Column(name = "muted_until")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime mutedUntil;
}
