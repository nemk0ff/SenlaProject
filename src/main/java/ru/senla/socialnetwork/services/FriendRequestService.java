package ru.senla.socialnetwork.services;

import java.util.List;
import ru.senla.socialnetwork.model.entities.FriendRequest;

public interface FriendRequestService {
  List<FriendRequest> getAll();

  List<FriendRequest> getByUserEmail(String userEmail);
}
