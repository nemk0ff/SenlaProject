package ru.senla.socialnetwork.dao.users;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.users.WallPost;

public interface WallPostDao extends GenericDao<WallPost> {
  List<WallPost> findAllByUser(Long userId);
}
