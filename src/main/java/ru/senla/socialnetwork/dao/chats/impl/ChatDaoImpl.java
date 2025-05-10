package ru.senla.socialnetwork.dao.chats.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.model.chats.Chat;

@Repository
@Slf4j
public class ChatDaoImpl extends HibernateAbstractDao<Chat> implements ChatDao {
  protected ChatDaoImpl(SessionFactory sessionFactory) {
    super(Chat.class, sessionFactory);
  }

  @Override
  public boolean existsByMembers(String email1, String email2) {
    log.info("Проверка существования чата между: {} и {}...", email1, email2);
    try {
      String query = "SELECT COUNT(cm1.chat.id) FROM ChatMember cm1 " +
          "JOIN ChatMember cm2 ON cm1.chat.id = cm2.chat.id " +
          "WHERE cm1.user.email = :email1 AND cm2.user.email = :email2 " +
          "AND cm1.chat.isGroup = false";
      Long count = sessionFactory.getCurrentSession()
          .createQuery(query, Long.class)
          .setParameter("email1", email1)
          .setParameter("email2", email2)
          .getSingleResult();

      if (count == 0) {
        log.info("Чат не найден.");
      } else {
        log.info("Чат найден.");
      }
      return count > 0;
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске чата по двум пользователям", e);
    }
  }

  @Override
  public Optional<Chat> find(Long id) {
    log.info("Поиск чата по id: {}", id);
    try {
      String hql = "SELECT c FROM Chat c LEFT JOIN FETCH c.members WHERE c.id = :id";
      return sessionFactory.getCurrentSession()
          .createQuery(hql, Chat.class)
          .setParameter("id", id)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске чата по id " + id, e);
    }
  }

  @Override
  public Optional<Chat> findWithMembersAndUsers(Long id) {
    log.info("Расширенный поиск чата по id: {}", id);
    try {
      String hql = "SELECT DISTINCT c FROM Chat c " +
          "LEFT JOIN FETCH c.members m " +
          "LEFT JOIN FETCH m.user " +
          "WHERE c.id = :chatId";

      return sessionFactory.getCurrentSession()
          .createQuery(hql, Chat.class)
          .setParameter("chatId", id)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при расширенном поиске чата по id " + id, e);
    }
  }

  @Override
  public List<Chat> findAllActiveByUserId(Long userId) {
    log.info("Поиск всех чатов для пользователя с id: {}", userId);
    try {
      String hql = "SELECT DISTINCT c FROM Chat c " +
          "LEFT JOIN FETCH c.members m " +
          "LEFT JOIN FETCH m.user " +
          "WHERE c.id IN (" +
          "SELECT c2.id FROM Chat c2 " +
          "JOIN c2.members m2 " +
          "WHERE m2.user.id = :userId " +
          "AND (m2.leaveDate IS NULL OR m2.joinDate > m2.leaveDate)" +
          ") " +
          "ORDER BY c.createdAt DESC";

      return sessionFactory.getCurrentSession()
          .createQuery(hql, Chat.class)
          .setParameter("userId", userId)
          .getResultList();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске чатов для пользователя с id " + userId, e);
    }
  }
}
