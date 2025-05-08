package ru.senla.socialnetwork.facades.comments;

import java.util.List;
import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;

public interface CommentFacade {
  List<CommentDTO> getAll();

  CommentDTO getById(Long id, String clientEmail);

  List<CommentDTO> getPostComments(Long postId, String email);

  CommentDTO create(CreateCommentDTO commentDTO, String clientEmail);

  CommentDTO update(UpdateCommentDTO commentDTO, String clientEmail);

  void delete(Long id, String clientEmail);
}
