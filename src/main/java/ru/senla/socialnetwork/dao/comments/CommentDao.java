package ru.senla.socialnetwork.dao.comments;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.comment.Comment;

public interface CommentDao extends GenericDao<Comment> {
  List<Comment> getAll();

  List<Comment> getAllByPost(Long postId);


}
