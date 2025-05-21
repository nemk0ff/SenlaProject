package ru.senla.socialnetwork.unit.facades.feed;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.unit.TestConstants.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.senla.socialnetwork.dto.PostDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;
import ru.senla.socialnetwork.facades.feed.impl.FeedFacadeImpl;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.posts.CommunityPostService;
import ru.senla.socialnetwork.services.posts.WallPostService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class FeedFacadeImplTest {
  @Mock
  private UserService userService;
  @Mock
  private FriendRequestService friendRequestService;
  @Mock
  private WallPostService wallPostService;
  @Mock
  private CommunityMemberService communityMemberService;
  @Mock
  private CommunityPostService communityPostService;

  @InjectMocks
  private FeedFacadeImpl feedFacade;

  private User testUser;
  private User friendUser;
  private Community testCommunity;
  private WallPost wallPost;
  private CommunityPost communityPost;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .build();

    friendUser = User.builder()
        .id(TEST_USER_ID_2)
        .email(TEST_EMAIL_2)
        .build();

    testCommunity = Community.builder()
        .id(TEST_COMMUNITY_ID)
        .name(TEST_COMMUNITY_NAME)
        .build();

    wallPost = WallPost.builder()
        .id(TEST_POST_ID)
        .body(TEST_BODY)
        .createdAt(TEST_DATE)
        .wallOwner(friendUser)
        .build();

    communityPost = CommunityPost.builder()
        .id(2L)
        .body("Community post")
        .createdAt(TEST_DATE.plusHours(1))
        .community(testCommunity)
        .build();
  }

  @Test
  void getNews_whenFriendsAndCommunitiesExist_thenReturnCombinedFeed() {
    List<User> friends = List.of(friendUser);
    CommunityMember member = CommunityMember.builder()
        .community(testCommunity)
        .build();
    List<CommunityMember> memberships = List.of(member);

    when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
    when(friendRequestService.getFriendsByUser(testUser.getId())).thenReturn(friends);
    when(communityMemberService.getAllByUser(testUser.getId())).thenReturn(memberships);

    when(wallPostService.getByUser(friendUser.getId())).thenReturn(List.of(wallPost));
    when(communityPostService.getAllPosts(testCommunity.getId())).thenReturn(List.of(communityPost));

    List<PostDTO> result = feedFacade.getNews(TEST_EMAIL_1);

    assertThat(result).hasSize(2);
    verify(userService).getUserByEmail(TEST_EMAIL_1);
    verify(friendRequestService).getFriendsByUser(testUser.getId());
    verify(communityMemberService).getAllByUser(testUser.getId());
  }
}