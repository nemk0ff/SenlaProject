package ru.senla.socialnetwork.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.model.entities.FriendRequest;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.repository.FriendRequestDao;

@Repository
@Slf4j
public class FriendRequestDaoImpl extends HibernateAbstractDao<FriendRequest>
    implements FriendRequestDao {
  public FriendRequestDaoImpl(SessionFactory sessionFactory) {
    super(FriendRequest.class, sessionFactory);
  }

  public List<FriendRequest> getAll() {
    log.info("Получение всех запросов на дружбу...");
    try {
      List<FriendRequest> friendRequests = sessionFactory.getCurrentSession()
          .createQuery("FROM FriendRequest ORDER BY id", FriendRequest.class)
          .getResultList();
      log.info("Найдено {} запросов", friendRequests.size());
      return friendRequests;
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при получении всех запросов на дружбу", e);
    }
  }

  @Override
  public List<FriendRequest> getByUserId(Long userId) {
    log.info("Получение запросов на дружбу для {}...", userId);
    try {
      String hql = "FROM FriendRequest WHERE sender.id = :userId OR recipient.id = :userId";
      List<FriendRequest> friendRequests = sessionFactory.getCurrentSession()
          .createQuery(hql, FriendRequest.class)
          .setParameter("userId", userId)
          .list();
      log.info("Найдено {} запросов дружить для {}", friendRequests.size(), userId);
      return friendRequests;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении запросов на дружбу для " + userId, e);
    }
  }
}
