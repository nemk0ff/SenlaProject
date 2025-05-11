package ru.senla.socialnetwork.facades.feed.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.PostDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityPostMapper;
import ru.senla.socialnetwork.dto.mappers.WallPostMapper;
import ru.senla.socialnetwork.facades.feed.FeedFacade;
import ru.senla.socialnetwork.model.Post;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FeedFacadeImpl implements FeedFacade {
  private final UserService userService;
  private final FriendRequestService friendRequestService;
  private final WallPostService wallPostService;
  private final CommunityMemberService communityMemberService;
  private final CommunityPostService communityPostService;

  @Override
  public List<PostDTO> getNews(String clientEmail) {
    User user = userService.getUserByEmail(clientEmail);

    List<User> friends = friendRequestService.getFriendsByUser(user.getId());

    List<CommunityMember> userIsMembers = communityMemberService.getAllByUser(user.getId());
    List<Community> communities = userIsMembers.stream()
        .map(CommunityMember::getCommunity)
        .toList();

    log.info("Нашли {} друзей и {} подписок на сообщества пользователя {}", friends.size(),
        communities.size(), clientEmail);

    List<WallPost> friendsPosts = friends.stream()
        .map(friend -> wallPostService.getByUser(friend.getId()))
        .flatMap(List::stream)
        .toList();
    List<CommunityPost> communitiesPosts = communities.stream()
        .map(community -> communityPostService.getAllPosts(community.getId()))
        .flatMap(List::stream)
        .toList();

    List<Post> allPosts = new ArrayList<>();
    allPosts.addAll(friendsPosts);
    allPosts.addAll(communitiesPosts);

    allPosts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
    log.info("Для пользователя {} собрано {} новостей", clientEmail, allPosts.size());
    return allPosts.stream()
        .map(post -> {
          if (post instanceof WallPost wallPost) {
            return WallPostMapper.INSTANCE.toDTO(wallPost);
          } else if (post instanceof CommunityPost communityPost) {
            return CommunityPostMapper.INSTANCE.toDTO(communityPost);
          }
          throw new IllegalStateException("Неизвестный тип поста: " + post.getClass());
        }).collect(Collectors.toList());
  }
}
