package ru.senla.socialnetwork.services.posts.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.communities.CommunityPostDao;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.exceptions.communities.CommunityPostException;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.services.posts.CommunityPostService;

@Slf4j
@Service
@AllArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {
  private final CommunityPostDao communityPostDao;

  @Override
  public List<CommunityPost> getAllPosts(Long communityId) {
    return communityPostDao.findAllByCommunity(communityId);
  }

  @Override
  public CommunityPost getPost(Long communityId, Long postId) {
    CommunityPost post = communityPostDao.find(postId)
        .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

    if (!post.getCommunity().getId().equals(communityId)) {
      throw new CommunityPostException("Пост не принадлежит этому сообществу");
    }
    return post;
  }

  @Override
  public CommunityPost createPost(Long communityId, CreateCommunityPostDTO dto,
                                  CommunityMember author) {
    CommunityPost post = CommunityPost.builder()
        .author(author)
        .body(dto.body())
        .isPinned(false)
        .createdAt(ZonedDateTime.now())
        .build();

    return communityPostDao.saveOrUpdate(post);
  }

  @Override
  public void deletePost(CommunityPost post) {
    communityPostDao.delete(post);
  }

  @Override
  public CommunityPost updatePost(CommunityPost post, UpdateCommunityPostDTO dto) {
    post.setBody(dto.body());
    return communityPostDao.saveOrUpdate(post);
  }
}
