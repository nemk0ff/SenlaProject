package ru.senla.socialnetwork.services.posts.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.users.WallPostDao;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.posts.WallPostService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class WallPostServiceImpl implements WallPostService {
  private final WallPostDao wallPostDao;

  @Override
  public List<WallPost> getByUser(Long userId) {
    return wallPostDao.findAllByUser(userId);
  }

  @Override
  public WallPost get(Long postId) {
    return wallPostDao.find(postId).orElseThrow(
        () -> new EntityNotFoundException("WallPost с id " + postId + " не найден"));
  }

  @Override
  public WallPost create(WallPostRequestDTO dto, User user) {
    WallPost newPost = WallPost.builder()
        .wallOwner(user)
        .body(dto.body())
        .mood(dto.mood())
        .location(dto.location())
        .build();
    return wallPostDao.saveOrUpdate(newPost);
  }

  @Override
  public WallPost update(WallPost post, WallPostRequestDTO dto) {
    post.setBody(dto.body());
    post.setMood(dto.mood());
    post.setLocation(dto.location());
    return wallPostDao.saveOrUpdate(post);
  }

  @Override
  public void delete(WallPost post) {
    wallPostDao.delete(post);
  }
}
