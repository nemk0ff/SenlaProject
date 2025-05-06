package ru.senla.socialnetwork.facades.comments;

import java.util.List;
import ru.senla.socialnetwork.model.comment.Reaction;
import ru.senla.socialnetwork.model.comment.ReactionType;

public interface CommentFacade {
  List<CommentDTO> getAll();

  CommentDTO getById(Long id);

  List<CommentDTO> getPostComments();

  CommentDTO create(CommentDTO commentDTO);

  CommentDTO update(CommentDTO commentDTO);

  void delete(Long id);
}
