package ru.senla.socialnetwork.facades.comments.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;
import ru.senla.socialnetwork.dto.mappers.CommentMapper;
import ru.senla.socialnetwork.exceptions.comments.CommentException;
import ru.senla.socialnetwork.facades.comments.CommentFacade;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.general.Post;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.WallPost;
import ru.senla.socialnetwork.services.comments.CommentService;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.friendRequest.FriendRequestService;
import ru.senla.socialnetwork.services.posts.GeneralPostService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentFacadeImpl implements CommentFacade {
  private final CommentService commentService;
  private final UserService userService;
  private final FriendRequestService friendRequestService;
  private final GeneralPostService generalPostService;
  private final CommentMapper commentMapper;
  private final CommunityMemberService communityMemberService;

  @Override
  public List<CommentDTO> getAll() {
    return commentMapper.toListDto(commentService.getAll());
  }

  @Override
  public CommentDTO getById(Long id, String clientEmail) {
    Comment comment = commentService.getById(id);
    User client = userService.getUserByEmail(clientEmail);
    if (!userService.isAdmin(clientEmail)) {
      if (comment.getPost().getPostType().equals("WallPost")) {
        WallPost post = (WallPost) comment.getPost();
        if (!client.equals(post.getWall_owner())
            && !friendRequestService.isFriends(post.getWall_owner().getId(), client.getId())
            && post.getWall_owner().getProfileType().equals(ProfileType.CLOSED)) {
          throw new CommentException("Вы не можете увидеть комментарий под этим постом, т.к. вы " +
              "не являетесь другом автора поста, а его профиль является закрытым");
        }
      }
    }
    return commentMapper.toDto(comment);
  }

  @Override
  public List<CommentDTO> getPostComments(Long postId, String clientEmail) {
    List<Comment> comments = commentService.getAllByPost(postId);
    Post post = generalPostService.getPost(postId);
    User client = userService.getUserByEmail(clientEmail);
    if (!userService.isAdmin(clientEmail)) {
      if (post.getPostType().equals("WallPost")) {
        WallPost wallpost = (WallPost) post;
        if (!client.equals(wallpost.getWall_owner())
            && !friendRequestService.isFriends(wallpost.getWall_owner().getId(), client.getId())
            && wallpost.getWall_owner().getProfileType().equals(ProfileType.CLOSED)) {
          throw new CommentException("Вы не можете увидеть комментарии под этим постом, т.к. вы " +
              "не являетесь другом автора поста, а его профиль является закрытым");
        }
      }
    }
    return commentMapper.toListDto(comments);
  }

  @Override
  public CommentDTO create(CreateCommentDTO commentDTO, String clientEmail) {
    Post post = generalPostService.getPost(commentDTO.postId());
    User client = userService.getUserByEmail(clientEmail);
    if (!userService.isAdmin(clientEmail)) {
      if (post.getPostType().equals("WallPost")) {
        WallPost wallpost = (WallPost) post;
        if (!friendRequestService.isFriends(wallpost.getWall_owner().getId(), client.getId())
            && wallpost.getWall_owner().getProfileType().equals(ProfileType.CLOSED)) {
          throw new CommentException("Вы не можете комментировать этот пост, т.к. вы " +
              "не являетесь другом автора поста, а его профиль является закрытым");
        }
      }
    }
    Comment createdComment = commentService.create(post, client, commentDTO.body());
    log.info("Комментарий создан: {}", createdComment);
    return commentMapper.toDto(createdComment);
  }

  @Override
  public CommentDTO update(UpdateCommentDTO commentDTO, String clientEmail) {
    Comment comment = commentService.getById(commentDTO.commentId());
    User client = userService.getUserByEmail(clientEmail);
    if (!client.equals(comment.getAuthor())) {
      throw new CommentException("Вы не можете редактировать чужие комментарии");
    }
    Comment updatedComment = commentService.update(comment, commentDTO.body());
    log.info("Комментарий обновлён: {}", updatedComment);
    return commentMapper.toDto(updatedComment);
  }

  @Override
  public void delete(Long id, String clientEmail) {
    Comment comment = commentService.getById(id);
    User client = userService.getUserByEmail(clientEmail);
    Post post = comment.getPost();

    // Если не админ и не автор комментария, то нужно проверить права
    if (!userService.isAdmin(clientEmail) && !client.equals(comment.getAuthor())) {
      if (post.getPostType().equals("WallPost")) {
        WallPost wallPost = (WallPost) comment.getPost();
        if (!wallPost.getWall_owner().equals(client)) {
          throw new CommentException("У вас нет прав для удаления этого комментария");
        }
      } else if (post.getPostType().equals("CommunityPost")) {
        CommunityPost communityPost = (CommunityPost) comment.getPost();
        CommunityMember member = communityMemberService.get(
            communityPost.getCommunity().getId(), clientEmail);
        if (member.getRole().equals(MemberRole.MEMBER)) {
          throw new CommentException("У вас нет прав для удаления этого комментария");
        }
      }
    }
    commentService.delete(comment);
    log.info("Комментарий {} удален", id);
  }
}
