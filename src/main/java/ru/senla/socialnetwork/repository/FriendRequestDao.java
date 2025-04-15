package ru.senla.socialnetwork.repository;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.model.entities.FriendRequest;

public interface FriendRequestDao {
  List<FriendRequest> getAll();

  List<FriendRequest> getByUserId(Long userId);
}
