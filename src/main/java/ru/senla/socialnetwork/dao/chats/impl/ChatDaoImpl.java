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

@Slf4j
@Repository
public class ChatDaoImpl extends HibernateAbstractDao<Chat> implements ChatDao {
  protected ChatDaoImpl(SessionFactory sessionFactory) {
    super(Chat.class, sessionFactory);
  }

  @Override
  public boolean existsByMembers(String email1, String email2) {
    log.info("Проверка существования чата между: {} и {}...", email1, email2);
    try {
      Long count = sessionFactory.getCurrentSession()
          .createNamedQuery("Chat.existsByMembers", Long.class)
          .setParameter("email1", email1)
          .setParameter("email2", email2)
          .getSingleResult();

      log.info("Выражение 'Чат между {} и {} существует' имеет значение {}",
          email1, email2, count > 0);
      return count > 0;
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске чата по двум пользователям", e);
    }
  }

  @Override
  public Optional<Chat> find(Long id) {
    log.info("Поиск чата по id: {}", id);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Chat.find", Chat.class)
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
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Chat.findWithMembersAndUsers", Chat.class)
          .setParameter("chatId", id)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при расширенном поиске чата по id " + id, e);
    }
  }

  @Override
  public List<Chat> findAllChatsByUserId(Long userId) {
    log.info("Поиск всех чатов для пользователя с id: {}", userId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Chat.findAllChatsByUserId", Chat.class)
          .setParameter("userId", userId)
          .getResultList();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске чатов для пользователя с id " + userId, e);
    }
  }
}
