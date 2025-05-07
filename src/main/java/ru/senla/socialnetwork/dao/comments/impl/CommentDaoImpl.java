package ru.senla.socialnetwork.dao.comments.impl;

import java.util.List;
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
  public List<Comment> getAll() {
    log.info("Получение списка всех комментариев...");
    try {
      String hql = "SELECT c FROM Comment c " +
          "LEFT JOIN FETCH c.author " +
          "LEFT JOIN FETCH c.post ";

      List<Comment> comments = sessionFactory.getCurrentSession()
          .createQuery(hql, Comment.class)
          .list();

      log.info("Найдено {} комментариев", comments.size());
      return comments;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех комментариев", e);
    }
  }

  @Override
  public List<Comment> getAllByPost(Long postId) {
    log.info("Получение списка всех комментариев поста {}...", postId);
    try {
      String hql = "SELECT c FROM Comment c LEFT JOIN FETCH c.author WHERE c.post.id = :postId";

      List<Comment> comments = sessionFactory.getCurrentSession()
          .createQuery(hql, Comment.class)
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

