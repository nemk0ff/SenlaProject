package ru.senla.socialnetwork.facades.comments.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.dto.mappers.ReactionMapper;
import ru.senla.socialnetwork.exceptions.comments.CommentException;
import ru.senla.socialnetwork.exceptions.comments.ReactionException;
import ru.senla.socialnetwork.facades.comments.ReactionFacade;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.comment.Reaction;
import ru.senla.socialnetwork.model.Post;
import ru.senla.socialnetwork.model.comment.ReactionType;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.comments.CommentService;
import ru.senla.socialnetwork.services.comments.ReactionService;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReactionFacadeImpl implements ReactionFacade {
  private final ReactionService reactionService;
  private final FriendRequestService friendRequestService;
  private final UserService userService;
  private final CommentService commentService;

  @Override
  public List<ReactionDTO> getAll() {
    return ReactionMapper.INSTANCE.toListDTO(reactionService.getAll());
  }

  @Override
  public List<ReactionDTO> getByComment(Long commentId, String clientEmail) {
    Comment comment = commentService.getById(commentId);
    User client = userService.getUserByEmail(clientEmail);
    if (!userService.isAdmin(clientEmail)) {
      if (comment.getPost().getPostType().equals("WallPost")) {
        WallPost wallpost = (WallPost) comment.getPost();
        if (!client.equals(wallpost.getWallOwner())
            && !friendRequestService.isFriends(wallpost.getWallOwner().getId(), client.getId())
            && wallpost.getWallOwner().getProfileType().equals(ProfileType.CLOSED)) {
          throw new CommentException("У вас нет доступа, т.к. вы не являетесь другом автора " +
              "поста, а его профиль является закрытым");
        }
      }
    }
    List<Reaction> reactions = reactionService.getAllByComment(commentId);
    return ReactionMapper.INSTANCE.toListDTO(reactions);
  }

  @Override
  public ReactionDTO getById(Long reactionId, String clientEmail) {
    Reaction reaction = reactionService.get(reactionId);
    Post post = reaction.getComment().getPost();
    User client = userService.getUserByEmail(clientEmail);

    if (userService.isAdmin(clientEmail)) {
      return ReactionMapper.INSTANCE.toDTO(reaction);
    }
    log.info("Пользователь запрашивает реакцию на комментарий поста {}", post.getPostType());
    if (post.getPostType().equals("WallPost")) {
      WallPost wallPost = (WallPost) post;
      User wallOwner = wallPost.getWallOwner();
      log.info("Проверяем, имеет ли {} доступ к стене пользователя {}", client, wallOwner.getEmail());
      // Если клиент - не владелец стены И профиль закрытый И они не друзья
      if (!client.equals(wallOwner)
          && wallOwner.getProfileType().equals(ProfileType.CLOSED)
          && !friendRequestService.isFriends(wallOwner.getId(), client.getId())) {
        throw new CommentException("Вы не можете увидеть комментарий под этим постом, т.к. вы " +
            "не являетесь другом автора поста, а его профиль является закрытым");
      }
    }
    return ReactionMapper.INSTANCE.toDTO(reaction);
  }

  @Override
  public ReactionDTO setReaction(Long commentId, ReactionType reactionType, String clientEmail) {
    Comment comment = commentService.getById(commentId);
    Post post = comment.getPost();
    User client = userService.getUserByEmail(clientEmail);
    if (!userService.isAdmin(clientEmail)) {
      if (post.getPostType().equals("WallPost")) {
        WallPost wallpost = (WallPost) post;
        if (!client.equals(wallpost.getWallOwner())
            && !friendRequestService.isFriends(wallpost.getWallOwner().getId(), client.getId())
            && wallpost.getWallOwner().getProfileType().equals(ProfileType.CLOSED)) {
          throw new CommentException("Вы не можете реагировать на этот комментарий" +
              "т.к. вы не являетесь другом автора поста, а его профиль является закрытым");
        }
      }
    }
    Reaction reaction = Reaction.builder()
        .owner(client)
        .type(reactionType)
        .comment(comment)
        .build();
    return ReactionMapper.INSTANCE.toDTO(reactionService.save(reaction));
  }

  @Override
  public void removeReaction(Long reactionId, String email) {
    Reaction reaction = reactionService.get(reactionId);
    User client = userService.getUserByEmail(email);
    if (!reaction.getOwner().equals(client)) {
      throw new ReactionException("У вас нет прав для удаления этой реакции");
    }
    reactionService.delete(reaction);
  }
}
