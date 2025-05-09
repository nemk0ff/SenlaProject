package ru.senla.socialnetwork.dao.friendRequests;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

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
      String hql = "SELECT fr.recipient FROM FriendRequest fr " +
          "WHERE fr.sender.id = :userId AND fr.status = 'ACCEPTED' " +
          "UNION " +
          "SELECT fr.sender FROM FriendRequest fr " +
          "WHERE fr.recipient.id = :userId AND fr.status = 'ACCEPTED'";

      List<User> friends = sessionFactory.getCurrentSession()
          .createQuery(hql, User.class)
          .setParameter("userId", userId)
          .getResultList();

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
  public boolean areFriends(Long firstUserId, Long secondUserId) {
    log.info("Проверка дружбы между user#{} и user#{}...", firstUserId, secondUserId);
    try {
      String hql = "SELECT COUNT(fr) > 0 FROM FriendRequest fr " +
          "WHERE ((fr.sender.id = :firstUser AND fr.recipient.id = :secondUser) " +
          "OR (fr.sender.id = :secondUser AND fr.recipient.id = :firstUser)) " +
          "AND fr.status = :status";

      Boolean areFriends = sessionFactory.getCurrentSession()
          .createQuery(hql, Boolean.class)
          .setParameter("firstUser", firstUserId)
          .setParameter("secondUser", secondUserId)
          .setParameter("status", FriendStatus.ACCEPTED)
          .getSingleResult();

      log.info("Пользователи user#{} и user#{} {} друзьями",
          firstUserId, secondUserId, areFriends ? "являются" : "не являются");
      return areFriends;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          String.format("Ошибка при проверке дружбы между user#%d и user#%d",
              firstUserId, secondUserId), e);
    }
  }
}
