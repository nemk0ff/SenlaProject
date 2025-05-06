package ru.senla.socialnetwork.facades.comments;

import java.util.List;
import ru.senla.socialnetwork.model.comment.Reaction;
import ru.senla.socialnetwork.model.comment.ReactionType;

public interface ReactionFacade {

  ReactionDTO setReaction(Long id, ReactionType type);

  void removeReaction(Long id);

  List<Reaction> getReactions(Long commentId);
}
