package ru.senla.socialnetwork.dao.users.impl;

import java.util.Optional;
import ru.senla.socialnetwork.dao.users.WallPostDao;
import ru.senla.socialnetwork.model.users.WallPost;

public class WallPostDaoImpl implements WallPostDao {
  @Override
  public WallPost saveOrUpdate(WallPost entity) {
    return null;
  }

  @Override
  public Optional<WallPost> find(Long id) {
    return Optional.empty();
  }

  @Override
  public void delete(WallPost entity) {

  }
}
