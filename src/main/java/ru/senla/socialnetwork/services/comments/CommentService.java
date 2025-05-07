package ru.senla.socialnetwork.services.comments;

import java.util.List;
import ru.senla.socialnetwork.model.comment.Comment;

public interface CommentService {
  List<Comment> getAll();

  List<Comment> getAllByPost(Long postId);

  Comment getById(Long commentId);
}
