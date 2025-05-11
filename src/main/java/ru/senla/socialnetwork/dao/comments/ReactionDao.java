package ru.senla.socialnetwork.dao.comments;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.comment.Reaction;

public interface ReactionDao extends GenericDao<Reaction> {
  List<Reaction> getAll();

  Optional<Reaction> get(Long id);

  List<Reaction> getAllByComment(Long commentId);

  Optional<Reaction> getByUserAndComment(Long userId, Long commentId);
}
