package ru.senla.socialnetwork.dao.comments.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.dao.comments.ReactionDao;
import ru.senla.socialnetwork.model.comment.Reaction;

@Repository
@Slf4j
public class ReactionDaoImpl extends HibernateAbstractDao<Reaction> implements ReactionDao {
  protected ReactionDaoImpl(SessionFactory sessionFactory) {
    super(Reaction.class, sessionFactory);
  }

  @Override
  public Optional<Reaction> get(Long id) {
    log.info("Получение реакции с id {} вместе с комментарием и постом...", id);
    try {
      String hql = "SELECT r FROM Reaction r " +
          "LEFT JOIN FETCH r.comment " +
          "LEFT JOIN FETCH r.comment.post "+
          "WHERE r.id = :id";
      return sessionFactory.getCurrentSession()
          .createQuery(hql, Reaction.class)
          .setParameter("id", id)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении реакции с id " + id, e);
    }
  }

  @Override
  public List<Reaction> getAll() {
    log.info("Получение списка всех реакций...");
    try {
      String hql = "SELECT r FROM Reaction r LEFT JOIN FETCH r.owner";

      List<Reaction> reactions = sessionFactory.getCurrentSession()
          .createQuery(hql, Reaction.class)
          .list();

      log.info("Найдено {} реакций", reactions.size());
      return reactions;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех реакций", e);
    }
  }

  @Override
  public List<Reaction> getAllByComment(Long commentId) {
    log.info("Получение списка всех реакций комментария {}...", commentId);
    try {
      String hql = "SELECT r FROM Reaction r LEFT JOIN FETCH r.owner " +
          "WHERE r.comment.id = :commentId";

      List<Reaction> reactions = sessionFactory.getCurrentSession()
          .createQuery(hql, Reaction.class)
          .setParameter("commentId", commentId)
          .list();

      log.info("Найдено {} реакций комментария {}", reactions.size(), commentId);
      return reactions;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех реакций комментария " + commentId, e);
    }
  }

  @Override
  public Optional<Reaction> getByUserAndComment(Long userId, Long commentId) {
    log.info("Поиск реакции по комментарию {} и пользователю {}...", commentId, userId);
    try {
      return sessionFactory.getCurrentSession()
          .createQuery("FROM Reaction WHERE comment.id = :commentId AND owner.id = :ownerId",
              Reaction.class)
          .setParameter("commentId", commentId)
          .setParameter("ownerId", userId)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске реакции для комментария" + commentId + " и пользователя" + userId);
    }
  }
}
