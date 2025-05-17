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
  public Optional<Reaction> find(Long id) {
    log.info("Получение реакции с id {} вместе с комментарием и постом...", id);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Reaction.find", Reaction.class)
          .setParameter("id", id)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении реакции с id " + id, e);
    }
  }

  @Override
  public List<Reaction> findAll() {
    log.info("Получение списка всех реакций...");
    try {
      List<Reaction> reactions = sessionFactory.getCurrentSession()
          .createNamedQuery("Reaction.findAll", Reaction.class)
          .list();

      log.info("Найдено {} реакций", reactions.size());
      return reactions;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех реакций", e);
    }
  }

  @Override
  public List<Reaction> findAllByComment(Long commentId) {
    log.info("Получение списка всех реакций комментария {}...", commentId);
    try {
      List<Reaction> reactions = sessionFactory.getCurrentSession()
          .createNamedQuery("Reaction.findAllByCommentId", Reaction.class)
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
  public Optional<Reaction> findByUserIdAndCommentId(Long userId, Long commentId) {
    log.info("Поиск реакции по комментарию {} и пользователю {}...", commentId, userId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Reaction.findByUserIdAndCommentId", Reaction.class)
          .setParameter("commentId", commentId)
          .setParameter("ownerId", userId)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске реакции для комментария" + commentId + " и пользователя" + userId);
    }
  }
}