package ru.senla.socialnetwork.dao.comments;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.comment.Comment;

public interface CommentDao extends GenericDao<Comment> {
  Optional<Comment> find(Long id);

  List<Comment> findAll();

  List<Comment> findAllByPostId(Long postId);
}
