package ru.senla.socialnetwork.dao.comments;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.comment.Reaction;

public interface ReactionDao extends GenericDao<Reaction> {
  List<Reaction> getAll();
}
