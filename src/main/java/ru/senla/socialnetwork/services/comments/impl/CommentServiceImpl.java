package ru.senla.socialnetwork.services.comments.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.comments.CommentDao;
import ru.senla.socialnetwork.exceptions.comments.CommentException;
import ru.senla.socialnetwork.model.comment.Comment;
import ru.senla.socialnetwork.model.Post;
import ru.senla.socialnetwork.model.users.User;
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
    return commentDao.getAllByPost(postId);
  }

  @Override
  public Comment getById(Long commentId) {
    return commentDao.getById(commentId)
        .orElseThrow(() -> new CommentException("Комментарий " + commentId + " не найден"));
  }

  @Override
  public Comment create(Post post, User author, String message) {
    Comment comment = Comment.builder()
        .post(post)
        .author(author)
        .body(message)
        .build();
    return commentDao.saveOrUpdate(comment);
  }

  @Override
  public Comment update(Comment comment, String newBody) {
    comment.setBody(newBody);
    return commentDao.saveOrUpdate(comment);
  }

  @Override
  public void delete(Comment comment) {
    commentDao.delete(comment);
  }
}
