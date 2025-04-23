package ru.senla.socialnetwork.dao.chats.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dao.impl.HibernateAbstractDao;
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


}
