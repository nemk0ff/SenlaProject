package ru.senla.socialnetwork.services.comments.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.comments.CommentDao;
import ru.senla.socialnetwork.exceptions.comments.CommentException;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.services.comments.CommentService;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
  private final CommentDao commentDao;

  @Override
  public List<Comment> getAll() {
    List<Comment> comments = commentDao.getAll();
    if(comments.isEmpty()) {
      throw new CommentException("Не найдено комментариев");
    }
    return comments;
  }

  @Override
  public List<Comment> getAllByPost(Long postId) {
    List<Comment> comments = commentDao.getAllByPost(postId);
    if(comments.isEmpty()) {
      throw new CommentException("Под этим постом нет комментариев. Прокомментируйте его первым!");
    }
    return comments;
  }

  @Override
  public Comment getById(Long commentId) {
    return commentDao.find(commentId)
        .orElseThrow(() -> new CommentException("Комментарий " + commentId + " не найден"));
  }
}
