package ru.senla.socialnetwork.model.chats;

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
import ru.senla.socialnetwork.model.general.ContentFragment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("MESSAGE")
public class ChatMessage extends ContentFragment {
  @ManyToOne
  @JoinColumn(name = "chat_id", nullable = false)
  private Chat chat;

  @ManyToOne
  @JoinColumn(name = "reply_to_id")
  private ChatMessage replyTo;

  @Column(name = "is_pinned")
  private Boolean isPinned;
}