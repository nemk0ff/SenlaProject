package ru.senla.socialnetwork.services.posts.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.communities.CommunityPostDao;
import ru.senla.socialnetwork.dao.users.WallPostDao;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.model.Post;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.posts.GeneralPostService;

@Slf4j
@Service
@AllArgsConstructor
public class GeneralPostServiceImpl implements GeneralPostService {
  private final CommunityPostDao communityPostDao;
  private final WallPostDao wallPostDao;

  @Override
  public Post getPost(Long postId) {
    Optional<WallPost> wallPost = wallPostDao.find(postId);
    if (wallPost.isPresent()) {
      return wallPost.get();
    }

    Optional<CommunityPost> communityPost = communityPostDao.find(postId);
    if (communityPost.isPresent()) {
      return communityPost.get();
    }

    throw new EntityNotFoundException("Пост " + postId + " не найден");
  }
}
