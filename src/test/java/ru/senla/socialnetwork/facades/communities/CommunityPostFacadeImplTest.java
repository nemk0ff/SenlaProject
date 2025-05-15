package ru.senla.socialnetwork.facades.communities;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.TestConstants.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.exceptions.communities.CommunityMemberException;
import ru.senla.socialnetwork.facades.communities.impl.CommunityPostFacadeImpl;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityService;
import ru.senla.socialnetwork.services.posts.CommunityPostService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class CommunityPostFacadeImplTest {
  @Mock
  private CommunityService communityService;
  @Mock
  private CommunityPostService communityPostService;
  @Mock
  private CommunityMemberService communityMemberService;
  @Mock
  private UserService userService;

  @InjectMocks
  private CommunityPostFacadeImpl communityPostFacade;

  private Community testCommunity;
  private CommunityMember testMember;
  private CommunityPost testPost;
  private CreateCommunityPostDTO createPostDTO;
  private UpdateCommunityPostDTO updatePostDTO;

  @BeforeEach
  void setUp() {
    User testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .build();

    testCommunity = Community.builder()
        .id(TEST_COMMUNITY_ID)
        .name(TEST_COMMUNITY_NAME)
        .build();

    testMember = CommunityMember.builder()
        .id(1L)
        .user(testUser)
        .community(testCommunity)
        .role(MemberRole.MEMBER)
        .build();

    testPost = CommunityPost.builder()
        .id(TEST_POST_ID)
        .author(testMember)
        .community(testCommunity)
        .body(TEST_BODY)
        .isPinned(false)
        .build();

    createPostDTO = new CreateCommunityPostDTO(
        TEST_BODY, false);

    updatePostDTO = new UpdateCommunityPostDTO(
        "Updated body", false);
  }

  @Nested
  class GetAllPostsTests {
    @Test
    void getAllPosts_whenCalled_thenReturnPostsList() {
      when(communityPostService.getAllPosts(TEST_COMMUNITY_ID))
          .thenReturn(List.of(testPost));

      List<CommunityPostDTO> result = communityPostFacade.getAllPosts(TEST_COMMUNITY_ID);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).body()).isEqualTo(TEST_BODY);
      verify(communityPostService).getAllPosts(TEST_COMMUNITY_ID);
    }
  }

  @Nested
  class GetPinnedPostsTests {
    @Test
    void getPinnedPosts_whenCalled_thenReturnPinnedPosts() {
      CommunityPost pinnedPost = CommunityPost.builder()
          .id(2L)
          .isPinned(true)
          .build();

      when(communityPostService.getPinnedPosts(TEST_COMMUNITY_ID))
          .thenReturn(List.of(pinnedPost));

      List<CommunityPostDTO> result = communityPostFacade.getPinnedPosts(TEST_COMMUNITY_ID);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).isPinned()).isTrue();
    }
  }

  @Nested
  class GetPostTests {
    @Test
    void getPost_whenExists_thenReturnPost() {
      when(communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID))
          .thenReturn(testPost);

      CommunityPostDTO result = communityPostFacade.getPost(TEST_COMMUNITY_ID, TEST_POST_ID);

      assertThat(result).isNotNull();
      assertThat(result.body()).isEqualTo(TEST_BODY);
    }
  }

  @Nested
  class CreatePostTests {
    @Test
    void createPost_whenValid_thenCreatePost() {
      when(communityService.get(TEST_COMMUNITY_ID)).thenReturn(testCommunity);
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(testMember);
      when(communityPostService.createPost(testCommunity, createPostDTO, testMember))
          .thenReturn(testPost);

      CommunityPostDTO result = communityPostFacade.createPost(
          TEST_COMMUNITY_ID, createPostDTO, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      verify(communityMemberService).checkIsBanned(TEST_COMMUNITY_ID, TEST_EMAIL_1);
      verify(communityPostService).createPost(testCommunity, createPostDTO, testMember);
    }

    @Test
    void createPost_whenBanned_thenThrowException() {
      doThrow(new CommunityMemberException("User is banned"))
          .when(communityMemberService).checkIsBanned(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThatThrownBy(() -> communityPostFacade.createPost(
          TEST_COMMUNITY_ID, createPostDTO, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class);
    }
  }

  @Nested
  class DeletePostTests {
    @Test
    void deletePost_whenAdmin_thenDelete() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(true);
      when(communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID)).thenReturn(testPost);

      communityPostFacade.deletePost(TEST_COMMUNITY_ID, TEST_POST_ID, TEST_EMAIL_1);

      verify(communityPostService).deletePost(testPost);
    }

    @Test
    void deletePost_whenAuthor_thenDelete() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID)).thenReturn(testPost);
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(testMember);

      communityPostFacade.deletePost(TEST_COMMUNITY_ID, TEST_POST_ID, TEST_EMAIL_1);

      verify(communityPostService).deletePost(testPost);
    }

    @Test
    void deletePost_whenNoAccess_thenThrowException() {
      CommunityMember otherMember = CommunityMember.builder()
          .id(2L)
          .user(User.builder().email("other@email.com").build())
          .build();

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID)).thenReturn(testPost);
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(otherMember);
      doThrow(new CommunityMemberException("No access"))
          .when(communityMemberService).checkIsAdminOrModer(TEST_COMMUNITY_ID, TEST_EMAIL_1);

      assertThatThrownBy(() -> communityPostFacade.deletePost(
          TEST_COMMUNITY_ID, TEST_POST_ID, TEST_EMAIL_1))
          .isInstanceOf(CommunityMemberException.class);
    }
  }

  @Nested
  class UpdatePostTests {
    @Test
    void updatePost_whenAuthor_thenUpdate() {
      CommunityPost updatedPost = CommunityPost.builder()
          .id(TEST_POST_ID)
          .body("Updated body")
          .build();

      when(communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID)).thenReturn(testPost);
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(testMember);
      when(communityPostService.updatePost(testPost, updatePostDTO)).thenReturn(updatedPost);

      CommunityPostDTO result = communityPostFacade.updatePost(
          TEST_COMMUNITY_ID, TEST_POST_ID, updatePostDTO, TEST_EMAIL_1);

      assertThat(result.body()).isEqualTo("Updated body");
    }

    @Test
    void updatePost_whenAdmin_thenUpdate() {
      CommunityMember adminMember = CommunityMember.builder()
          .role(MemberRole.ADMIN)
          .build();

      when(communityPostService.getPost(TEST_COMMUNITY_ID, TEST_POST_ID)).thenReturn(testPost);
      when(communityMemberService.get(TEST_COMMUNITY_ID, TEST_EMAIL_1)).thenReturn(adminMember);
      when(communityPostService.updatePost(testPost, updatePostDTO)).thenReturn(testPost);

      CommunityPostDTO result = communityPostFacade.updatePost(
          TEST_COMMUNITY_ID, TEST_POST_ID, updatePostDTO, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      verify(communityMemberService).checkIsAdminOrModer(TEST_COMMUNITY_ID, TEST_EMAIL_1);
    }
  }
}