package ru.senla.socialnetwork.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.model.entities.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.entities.users.User;
import ru.senla.socialnetwork.model.enums.FriendStatus;
import ru.senla.socialnetwork.repository.FriendRequestDao;

@Repository
@Slf4j
public class FriendRequestDaoImpl extends HibernateAbstractDao<FriendRequest>
    implements FriendRequestDao {
  public FriendRequestDaoImpl(SessionFactory sessionFactory) {
    super(FriendRequest.class, sessionFactory);
  }

  @Override
  public List<User> findFriendsByUserId(Long userId) {
    log.info("Получение списка друзей для user#{}...", userId);
    try {
      String hql = "SELECT CASE WHEN fr.sender.id = :userId THEN fr.recipient ELSE fr.sender END " +
          "FROM FriendRequest fr " +
          "WHERE (fr.sender.id = :userId OR fr.recipient.id = :userId) " +
          "AND fr.status = :status";

      List<User> friends = sessionFactory.getCurrentSession()
          .createQuery(hql, User.class)
          .setParameter("userId", userId)
          .setParameter("status", FriendStatus.ACCEPTED)
          .list();

      log.info("Найдено {} друзей для user#{}", friends.size(), userId);
      return friends;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка друзей для user#" + userId, e);
    }
  }

  @Override
  public List<FriendRequest> getAllByUserId(Long userId) {
    log.info("Получение friendRequests для user#{}...", userId);
    try {
      String hql = "FROM FriendRequest WHERE sender.id = :userId OR recipient.id = :userId";
      List<FriendRequest> friendRequests = sessionFactory.getCurrentSession()
          .createQuery(hql, FriendRequest.class)
          .setParameter("userId", userId)
          .list();
      log.info("Найдено {} friendRequests для user#{}", friendRequests.size(), userId);
      return friendRequests;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении friendRequests для user#" + userId, e);
    }
  }

  @Override
  public Optional<FriendRequest> getByUsersIds(Long firstUser, Long secondUser,
                                               boolean isOrderRelevant) {
    log.info("Поиск friendRequests для {} и {}...", firstUser, secondUser);
    try {
      String hql = "FROM FriendRequest WHERE " +
          "(sender.id = :firstUser AND recipient.id = :secondUser) ";
      hql = isOrderRelevant ?
          hql : hql + "OR (sender.id = :secondUser AND recipient.id = :firstUser)";

      Optional<FriendRequest> request = sessionFactory.getCurrentSession()
          .createQuery(hql, FriendRequest.class)
          .setParameter("firstUser", firstUser)
          .setParameter("secondUser", secondUser)
          .uniqueResultOptional();
      if (request.isEmpty()) {
        log.info("FriendRequests для {} и {} не найден", firstUser, secondUser);
      } else {
        log.info("Найден friendRequests для {} и {}: {}", firstUser, secondUser, request.get());
      }
      return request;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске friendRequests для " + firstUser + " и " + secondUser);
    }
  }

  @Override
  public FriendRequest add(FriendRequest friendRequest) {
    log.debug("Добавление friendRequest в бд: {}...", friendRequest);
    try {
      sessionFactory.getCurrentSession().persist(friendRequest);
      log.info("FriendRequest успешно добавлен в бд: {}", friendRequest);
      return friendRequest;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Не удалось добавить friendRequest: " + e.getMessage(), e);
    }
  }
}
