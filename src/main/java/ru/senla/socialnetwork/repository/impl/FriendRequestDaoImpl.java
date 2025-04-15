package ru.senla.socialnetwork.repository.impl;

import java.util.Optional;
import ru.senla.socialnetwork.model.entities.FriendRequest;
import ru.senla.socialnetwork.repository.FriendRequestDao;

public class FriendRequestDaoImpl implements FriendRequestDao {
  public Optional<FriendRequest> getByUserId(Long userId) {
    return Optional.empty();
  }
}
