package ru.senla.socialnetwork.dao.chats.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.MemberRole;

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
  public List<ChatMember> findAllByChatId(Long chatId) {
    log.info("Поиск участников чата {}", chatId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("ChatMember.findAllByChatId", ChatMember.class)
          .setParameter("chatId", chatId)
          .getResultList();
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске участников чата " + chatId, e);
    }
  }

  @Override
  public Optional<ChatMember> findByChatIdAndUserEmail(Long chatId, String userEmail) {
    log.info("Поиск участника чата {} с email {}", chatId, userEmail);
    try {
      Optional<ChatMember> member = sessionFactory.getCurrentSession()
          .createNamedQuery("ChatMember.findByChatIdAndUserEmail", ChatMember.class)
          .setParameter("chatId", chatId)
          .setParameter("email", userEmail)
          .uniqueResultOptional();

      log.info("Найден участник: {}", member.orElse(null));
      return member;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске активного участника чата", e);
    }
  }

  @Override
  public Optional<ChatMember> findActiveByChatIdAndUserEmail(Long chatId, String userEmail) {
    log.info("Поиск активного участника чата {} с email {}", chatId, userEmail);
    try {
      Optional<ChatMember> member = sessionFactory.getCurrentSession()
          .createNamedQuery("ChatMember.findActiveByChatIdAndUserEmail", ChatMember.class)
          .setParameter("chatId", chatId)
          .setParameter("email", userEmail)
          .uniqueResultOptional();

      log.info("Найден активный участник: {}", member.orElse(null));
      return member;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при поиске активного участника чата", e);
    }
  }

  @Override
  public long countByChatIdAndRole(Long chatId, MemberRole role) {
    log.info("Подсчет активных участников чата {} с ролью {}", chatId, role);
    try {
      Long count = sessionFactory.getCurrentSession()
          .createNamedQuery("ChatMember.countByChatIdAndRole", Long.class)
          .setParameter("chatId", chatId)
          .setParameter("role", role)
          .getSingleResult();

      log.info("Найдено {} активных участников с ролью {} в чате {}", count, role, chatId);
      return count != null ? count : 0L;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при подсчете активных участников чата", e);
    }
  }

  @Override
  public long countByChatId(Long chatId) {
    log.info("Подсчет активных участников чата {}", chatId);
    try {
      Long count = sessionFactory.getCurrentSession()
          .createNamedQuery("ChatMember.countByChatId", Long.class)
          .setParameter("chatId", chatId)
          .getSingleResult();

      log.info("В чате {} найдено {} активных участников", chatId, count);
      return count != null ? count : 0L;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при подсчете активных участников чата", e);
    }
  }
}