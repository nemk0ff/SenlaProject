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
import ru.senla.socialnetwork.facades.comments.CommentFacade;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.comments.CommentService;
import ru.senla.socialnetwork.services.communities.CommunityPostService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentFacadeImpl implements CommentFacade {
  private final CommentService commentService;
  private final UserService userService;
  private final CommunityPostService postService;
  private final CommentMapper commentMapper;

  @Override
  public List<CommentDTO> getAll() {
    return commentMapper.toListDto(commentService.getAll());
  }

  @Override
  public CommentDTO getById(Long id) {
    return commentMapper.toDto(commentService.getById(id));
  }

  @Override
  public List<CommentDTO> getPostComments(Long postId, String email) {

  }

  @Override
  public CommentDTO create(CreateCommentDTO commentDTO) {
    return null;
  }

  @Override
  public CommentDTO update(UpdateCommentDTO commentDTO) {
    return null;
  }

  @Override
  public void delete(Long id) {
  }
}
