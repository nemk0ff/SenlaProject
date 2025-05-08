package ru.senla.socialnetwork.services.comments;

import java.util.List;
import ru.senla.socialnetwork.model.comment.Reaction;

public interface ReactionService {
  List<Reaction> getAll();

  List<Reaction> getAllByComment(Long commentId);

  Reaction get(Long reactionId);

  Reaction save(Reaction reaction);

  void delete(Reaction reaction);
}
