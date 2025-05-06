package ru.senla.socialnetwork.dao.comments.impl;

import java.util.List;
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
}
