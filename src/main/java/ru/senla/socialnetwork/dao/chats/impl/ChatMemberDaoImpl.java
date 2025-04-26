package ru.senla.socialnetwork.dao.chats.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.dao.impl.HibernateAbstractDao;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;

@Slf4j
@Repository
public class ChatMemberDaoImpl extends HibernateAbstractDao<ChatMember> implements ChatMemberDao {

  public ChatMemberDaoImpl(SessionFactory sessionFactory) {
    super(ChatMember.class, sessionFactory);
  }

  @Override
  public void saveAll(List<ChatMember> members) {
    Session session = sessionFactory.getCurrentSession();
    for (ChatMember member : members) {
      session.persist(member);
    }
  }

  @Override
  public List<ChatMember> findMembersByChatId(Long chatId) {
    return sessionFactory.getCurrentSession()
        .createQuery("FROM ChatMember WHERE chat.id = :chatId", ChatMember.class)
        .setParameter("chatId", chatId)
        .getResultList();
  }

  @Override
  public Optional<ChatMember> findByChatIdAndUserEmail(Long chatId, String userEmail) {
    log.info("Поиск участника чата {} с email {}", chatId, userEmail);
    try {
      String hql = "FROM ChatMember cm " +
          "WHERE cm.chat.id = :chatId " +
          "AND cm.user.email = :email";

      Optional<ChatMember> member = sessionFactory.getCurrentSession()
          .createQuery(hql, ChatMember.class)
          .setParameter("chatId", chatId)
          .setParameter("email", userEmail)
          .uniqueResultOptional();

      log.info("Найден участник: {}", member.orElse(null));
      return member;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске участника чата", e);
    }
  }

  @Override
  public boolean existsByChatIdAndUserEmail(Long chatId, String userEmail) {
    log.info("Проверка существования участника {} в чате {}", userEmail, chatId);
    try {
      String hql = "SELECT COUNT(cm) > 0 FROM ChatMember cm " +
          "WHERE cm.chat.id = :chatId " +
          "AND cm.user.email = :email";

      boolean exists = sessionFactory.getCurrentSession()
          .createQuery(hql, Boolean.class)
          .setParameter("chatId", chatId)
          .setParameter("email", userEmail)
          .getSingleResult();

      log.info("Участник {} {} в чате {}",
          userEmail, exists ? "найден" : "не найден", chatId);
      return exists;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при проверке существования участника", e);
    }
  }

  @Override
  public long countByChatIdAndRole(Long chatId, MemberRole role) {
    log.info("Подсчет участников чата {} с ролью {}", chatId, role);
    try {
      String hql = "SELECT COUNT(cm) FROM ChatMember cm " +
          "WHERE cm.chat.id = :chatId " +
          "AND cm.role = :role";

      Long count = sessionFactory.getCurrentSession()
          .createQuery(hql, Long.class)
          .setParameter("chatId", chatId)
          .setParameter("role", role)
          .getSingleResult();

      log.info("Найдено {} участников с ролью {} в чате {}", count, role, chatId);
      return count != null ? count : 0L;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при подсчете участников чата", e);
    }
  }

  @Override
  public long countByChatId(Long chatId) {
    log.info("Подсчет общего количества участников чата {}", chatId);
    try {
      String hql = "SELECT COUNT(cm) FROM ChatMember cm " +
          "WHERE cm.chat.id = :chatId";

      Long count = sessionFactory.getCurrentSession()
          .createQuery(hql, Long.class)
          .setParameter("chatId", chatId)
          .getSingleResult();

      log.info("В чате {} найдено {} участников", chatId, count);
      return count != null ? count : 0L;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при подсчете участников чата", e);
    }
  }
}
