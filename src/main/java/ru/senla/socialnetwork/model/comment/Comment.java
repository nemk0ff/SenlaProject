package ru.senla.socialnetwork.model.comment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.senla.socialnetwork.model.ContentFragment;
import ru.senla.socialnetwork.model.Post;

@Entity
@DiscriminatorValue("COMMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@NamedQuery(name = "Comment.find",
    query = "SELECT c FROM Comment c JOIN FETCH c.post JOIN FETCH c.author " +
        "JOIN FETCH c.reactions WHERE c.id = :id")
@NamedQuery(name = "Comment.findAll",
    query = "SELECT c FROM Comment c JOIN FETCH c.post JOIN FETCH c.author JOIN FETCH c.reactions")
@NamedQuery(name = "Comment.findAllByPostId",
    query = "SELECT c FROM Comment c JOIN FETCH c.post JOIN FETCH c.author " +
        "JOIN FETCH c.reactions WHERE c.post.id = :postId")
public final class Comment extends ContentFragment {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Reaction> reactions;
}
