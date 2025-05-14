package ru.senla.socialnetwork.model.chats;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.senla.socialnetwork.model.ContentFragment;

@Entity
@DiscriminatorValue("MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@NamedQuery(name = "Message.find",
    query = "FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdAt")
@NamedQuery(name = "Message.findAnswers",
    query = "FROM Message m JOIN FETCH m.author JOIN FETCH m.chat JOIN FETCH m.replyTo" +
        " WHERE m.chat.id = :chatId AND m.replyTo.id = :messageId " +
        "ORDER BY m.createdAt DESC")
@NamedQuery(name = "Message.findPinnedByChatId",
    query = "FROM Message m JOIN FETCH m.author JOIN FETCH m.chat JOIN FETCH m.replyTo" +
        "WHERE m.chat.id = :chatId AND m.isPinned = true " +
        "ORDER BY m.createdAt DESC")
@NamedQuery(name = "Message.findByIdAndChatId",
    query = "FROM Message m WHERE m.chat.id = :chatId AND m.id = :messageId")
public final class Message extends ContentFragment {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_id", nullable = false)
  private Chat chat;

  @ManyToOne
  @JoinColumn(name = "reply_to_id")
  private Message replyTo;

  @Column(name = "is_pinned")
  private Boolean isPinned;
}