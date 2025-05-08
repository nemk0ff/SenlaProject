package ru.senla.socialnetwork.services.comments;

import java.util.List;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.general.Post;
import ru.senla.socialnetwork.model.users.User;

public interface CommentService {
  List<Comment> getAll();

  List<Comment> getAllByPost(Long postId);

  Comment getById(Long commentId);

  Comment create(Post post, User author, String message);

  Comment update(Comment comment, String newBody);

  void delete(Comment comment);
}
