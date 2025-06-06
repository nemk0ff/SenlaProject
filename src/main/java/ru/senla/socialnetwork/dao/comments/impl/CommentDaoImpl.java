package ru.senla.socialnetwork.dao.comments.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.dao.comments.CommentDao;
import ru.senla.socialnetwork.model.comment.Comment;

@Repository
@Slf4j
public class CommentDaoImpl extends HibernateAbstractDao<Comment> implements CommentDao {
  protected CommentDaoImpl(SessionFactory sessionFactory) {
    super(Comment.class, sessionFactory);
  }

  @Override
  public Optional<Comment> find(Long id) {
    log.info("Получение комментария с id {} вместе с постом...", id);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Comment.find", Comment.class)
          .setParameter("id", id)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении комментария с id " + id, e);
    }
  }

  @Override
  public List<Comment> findAll() {
    log.info("Получение списка всех комментариев...");
    try {
      List<Comment> comments = sessionFactory.getCurrentSession()
          .createNamedQuery("Comment.findAll", Comment.class)
          .list();

      log.info("Найдено {} комментариев", comments.size());
      return comments;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех комментариев", e);
    }
  }

  @Override
  public List<Comment> findAllByPostId(Long postId) {
    log.info("Получение списка всех комментариев поста {}...", postId);
    try {
      List<Comment> comments = sessionFactory.getCurrentSession()
          .createNamedQuery("Comment.findAllByPostId", Comment.class)
          .setParameter("postId", postId)
          .list();

      log.info("Найдено {} комментариев поста {}", comments.size(), postId);
      return comments;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех комментариев поста " + postId, e);
    }
  }
}

