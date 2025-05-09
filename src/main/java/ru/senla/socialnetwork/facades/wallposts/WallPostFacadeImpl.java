package ru.senla.socialnetwork.facades.wallposts;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.mappers.WallPostMapper;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;
import ru.senla.socialnetwork.exceptions.users.WallPostException;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.user.UserService;
import ru.senla.socialnetwork.services.posts.WallPostService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WallPostFacadeImpl implements WallPostFacade {
  private final UserService userService;
  private final FriendRequestService friendRequestService;
  private final WallPostService wallPostService;

  @Override
  public List<WallPostResponseDTO> getByUser(String email, String clientEmail) {
    User postsOwner = userService.getUserByEmail(email);
    User client = userService.getUserByEmail(clientEmail);

    boolean hasAccess = userService.isAdmin(clientEmail)
        || friendRequestService.isFriends(postsOwner.getId(), client.getId())
        || email.equals(clientEmail);
    if (hasAccess) {
      return WallPostMapper.INSTANCE.toListDTO(wallPostService.getByUser(postsOwner.getId()));
    }
    throw new WallPostException("У вас нет доступа к просмотру стены пользователя " + email);
  }

  @Override
  public WallPostResponseDTO getById(Long postId, String clientEmail) {
    WallPost post = wallPostService.get(postId);
    User postAuthor = post.getWall_owner();
    User client = userService.getUserByEmail(clientEmail);

    boolean hasAccess = userService.isAdmin(clientEmail)
        || friendRequestService.isFriends(postAuthor.getId(), client.getId())
        || postAuthor.getEmail().equals(clientEmail);
    if (hasAccess) {
      return WallPostMapper.INSTANCE.toDTO(post);
    }
    throw new WallPostException("У вас нет доступа к просмотру стены пользователя " + postAuthor.getEmail());
  }

  @Override
  public WallPostResponseDTO create(WallPostRequestDTO dto, String clientEmail) {
    User user = userService.getUserByEmail(clientEmail);
    WallPost savedPost = wallPostService.create(dto, user);
    log.info("Пост создан: {}", savedPost);
    return WallPostMapper.INSTANCE.toDTO(savedPost);
  }

  @Override
  public void delete(Long postId, String clientEmail) {
    User user = userService.getUserByEmail(clientEmail);
    WallPost post = wallPostService.get(postId);
    if(post.getWall_owner().equals(user) || userService.isAdmin(clientEmail)) {
      wallPostService.delete(post);
      log.info("Пост удалён.");
    }
    throw new WallPostException("У вас не хватает прав для удаления этого поста");
  }

  @Override
  public WallPostResponseDTO update(Long postId, WallPostRequestDTO dto, String clientEmail) {
    User user = userService.getUserByEmail(clientEmail);
    WallPost post = wallPostService.get(postId);
    if(post.getWall_owner().equals(user)) {
      WallPost updatedPost = wallPostService.update(post, dto);
      log.info("Пост обновлён: {}", updatedPost);
      return WallPostMapper.INSTANCE.toDTO(updatedPost);
    }
    throw new WallPostException("У вас не хватает прав для удаления этого поста");
  }
}
