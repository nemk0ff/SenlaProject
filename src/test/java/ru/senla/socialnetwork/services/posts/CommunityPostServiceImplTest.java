package ru.senla.socialnetwork.services.posts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.socialnetwork.dao.communities.CommunityPostDao;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.exceptions.communities.CommunityPostException;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.TestConstants.*;
import ru.senla.socialnetwork.services.posts.impl.CommunityPostServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommunityPostServiceImplTest {
  @Mock
  private CommunityPostDao communityPostDao;

  @InjectMocks
  private CommunityPostServiceImpl communityPostService;

  private Community testCommunity;
  private CommunityMember testAuthor;
  private CommunityPost testPost;
  private CreateCommunityPostDTO createDto;
  private UpdateCommunityPostDTO updateDto;

  @BeforeEach
  void setUp() {
    testCommunity = Community.builder()
        .id(TEST_COMMUNITY_ID)
        .name(TEST_COMMUNITY_NAME)
        .build();

    testAuthor = CommunityMember.builder()
        .id(TEST_USER_ID_1)
        .build();

    testPost = CommunityPost.builder()
        .id(TEST_POST_ID)
        .community(testCommunity)
        .author(testAuthor)
        .body(TEST_BODY)
        .isPinned(false)
        .createdAt(ZonedDateTime.now())
        .build();

    createDto = new CreateCommunityPostDTO(TEST_BODY, false);
    updateDto = new UpdateCommunityPostDTO("Updated body", true);
  }

  @Nested
  class GetAllPostsTests {
    @Test
    void getAllPosts_whenPostsExist_thenReturnPosts() {
      when(communityPostDao.findAllByCommunity(TEST_COMMUNITY_ID))
          .thenReturn(List.of(testPost));

      List<CommunityPost> result = communityPostService.getAllPosts(TEST_COMMUNITY_ID);

      assertThat(result).containsExactly(testPost);
      verify(communityPostDao).findAllByCommunity(TEST_COMMUNITY_ID);
    }

    @Test
    void getAllPosts_whenNoPosts_thenReturnEmptyList() {
      when(communityPostDao.findAllByCommunity(TEST_COMMUNITY_ID))
          .thenReturn(List.of());

      List<CommunityPost> result = communityPostService.getAllPosts(TEST_COMMUNITY_ID);

      assertThat(result).isEmpty();
      verify(communityPostDao).findAllByCommunity(TEST_COMMUNITY_ID);
    }
  }

  @Nested
  class GetPinnedPostsTests {
    @Test
    void getPinnedPosts_whenPostsExist_thenReturnPosts() {
      CommunityPost pinnedPost = CommunityPost.builder()
          .id(2L)
          .community(testCommunity)
          .isPinned(true)
          .build();

      when(communityPostDao.findPinnedByCommunity(TEST_COMMUNITY_ID))
          .thenReturn(List.of(pinnedPost));

      List<CommunityPost> result = communityPostService.getPinnedPosts(TEST_COMMUNITY_ID);

      assertThat(result).containsExactly(pinnedPost);
      assertThat(result.get(0).getIsPinned()).isTrue();
      verify(communityPostDao).findPinnedByCommunity(TEST_COMMUNITY_ID);
    }

    @Test
    void getPinnedPosts_whenNoPosts_thenReturnEmptyList() {
      when(communityPostDao.findPinnedByCommunity(TEST_COMMUNITY_ID))
          .thenReturn(List.of());

      List<CommunityPost> result = communityPostService.getPinnedPosts(TEST_COMMUNITY_ID);

      assertThat(result).isEmpty();
      verify(communityPostDao).findPinnedByCommunity(TEST_COMMUNITY_ID);
    }
  }

  @Nested
  class GetPostTests {
    @Test
    void getPost_whenPostExistsAndBelongsToCommunity_thenReturnPost() {
      when(communityPostDao.find(TEST_POST_ID))
          .thenReturn(Optional.of(testPost));

      CommunityPost result = communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID);

      assertThat(result).isEqualTo(testPost);
      verify(communityPostDao).find(TEST_POST_ID);
    }

    @Test
    void getPost_whenPostNotExists_thenThrowException() {
      when(communityPostDao.find(TEST_POST_ID))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Пост не найден");
    }

    @Test
    void getPost_whenPostFromDifferentCommunity_thenThrowException() {
      Community otherCommunity = Community.builder()
          .id(999L)
          .build();
      CommunityPost otherPost = CommunityPost.builder()
          .id(TEST_POST_ID)
          .community(otherCommunity)
          .build();

      when(communityPostDao.find(TEST_POST_ID))
          .thenReturn(Optional.of(otherPost));

      assertThatThrownBy(() -> communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID))
          .isInstanceOf(CommunityPostException.class)
          .hasMessageContaining("Пост не принадлежит этому сообществу");
    }
  }

  @Nested
  class CreatePostTests {
    @Test
    void createPost_whenValidData_thenReturnCreatedPost() {
      when(communityPostDao.saveOrUpdate(any(CommunityPost.class)))
          .thenReturn(testPost);

      CommunityPost result = communityPostService.createPost(
          testCommunity, createDto, testAuthor);

      assertThat(result).isEqualTo(testPost);
      assertThat(result.getCommunity()).isEqualTo(testCommunity);
      assertThat(result.getAuthor()).isEqualTo(testAuthor);
      assertThat(result.getBody()).isEqualTo(TEST_BODY);
      verify(communityPostDao).saveOrUpdate(any(CommunityPost.class));
    }
  }

  @Nested
  class DeletePostTests {
    @Test
    void deletePost_whenValidPost_thenCallDao() {
      communityPostService.deletePost(testPost);

      verify(communityPostDao).delete(testPost);
    }
  }

  @Nested
  class UpdatePostTests {
    @Test
    void updatePost_whenValidData_thenReturnUpdatedPost() {
      when(communityPostDao.saveOrUpdate(any(CommunityPost.class)))
          .thenAnswer(invocation -> {
            CommunityPost p = invocation.getArgument(0);
            p.setBody(updateDto.body());
            p.setIsPinned(updateDto.isPinned());
            return p;
          });

      CommunityPost result = communityPostService.updatePost(testPost, updateDto);

      assertThat(result.getBody()).isEqualTo(updateDto.body());
      assertThat(result.getIsPinned()).isEqualTo(updateDto.isPinned());
      verify(communityPostDao).saveOrUpdate(testPost);
    }
  }
}