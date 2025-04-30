package ru.senla.socialnetwork.services.communities.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.communities.CommunityPostDao;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.exceptions.communities.CommunityException;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.services.communities.CommunityPostService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {
  private final CommunityPostDao communityPostDao;

  @Override
  public CommunityPost createPost(Long communityId, CreateCommunityPostDTO dto,
                                  CommunityMember author) {
    CommunityPost post = CommunityPost.builder()
        .author_id(author)
        .body(dto.body())
        .tags(dto.tags())
        .isPinned(false)
        .created_at(ZonedDateTime.now())
        .build();

    return communityPostDao.saveOrUpdate(post);
  }

  @Override
  public void deletePost(Long communityId, Long postId) {
    CommunityPost post = getPost(communityId, postId);
    communityPostDao.delete(post);
  }

  @Override
  public CommunityPost updatePost(Long communityId, Long postId, UpdateCommunityPostDTO dto) {
    CommunityPost post = getPost(communityId, postId);

    post.setBody(dto.body());
    if (dto.tags() != null) {
      post.setTags(dto.tags());
    }
    return communityPostDao.saveOrUpdate(post);
  }

  @Override
  public List<CommunityPost> getAllPosts(Long communityId) {
    return communityPostDao.findAllByCommunity(communityId);
  }

  private CommunityPost getPost(Long communityId, Long postId) {
    CommunityPost post = communityPostDao.find(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));

    if (!post.getAuthor_id().getCommunity().getId().equals(communityId)) {
      throw new CommunityException("Пост не принадлежит этому сообществу");
    }
    return post;
  }
}
