package ru.senla.socialnetwork.dao.comments;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.comment.Reaction;

public interface ReactionDao extends GenericDao<Reaction> {
  List<Reaction> findAll();

  Optional<Reaction> find(Long id);

  List<Reaction> findAllByComment(Long commentId);

  Optional<Reaction> findByUserIdAndCommentId(Long userId, Long commentId);
}
