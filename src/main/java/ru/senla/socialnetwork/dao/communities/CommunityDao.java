package ru.senla.socialnetwork.dao.communities;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.communities.Community;

public interface CommunityDao extends GenericDao<Community> {
  List<Community> getAll();
}
