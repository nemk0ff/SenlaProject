package ru.senla.socialnetwork.model.chats;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.senla.socialnetwork.model.general.GroupMember;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("CHAT")
public final class ChatMember extends GroupMember {
  @ManyToOne
  @JoinColumn(name = "chat_id")
  private Chat chat;

  @Column(name = "muted_until")
  private ZonedDateTime mutedUntil;
}
