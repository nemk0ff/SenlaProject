package ru.senla.socialnetwork.services.posts;

import java.util.List;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;

public interface WallPostService {
  List<WallPost> getByUser(Long userId);

  WallPost get(Long postId);

  WallPost create(WallPostRequestDTO dto, User user);

  WallPost update(WallPost post, WallPostRequestDTO dto);

  void delete(WallPost post);
}
