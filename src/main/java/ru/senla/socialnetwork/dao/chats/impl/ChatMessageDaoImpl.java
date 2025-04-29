package ru.senla.socialnetwork.dao.chats.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.chats.ChatMessageDao;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.model.chats.ChatMessage;

@Slf4j
@Repository
public class ChatMessageDaoImpl extends HibernateAbstractDao<ChatMessage> implements ChatMessageDao {
  protected ChatMessageDaoImpl(SessionFactory sessionFactory) {
    super(ChatMessage.class, sessionFactory);
  }

  @Override
  public List<ChatMessage> findByChatId(Long chatId) {
    log.info("Поиск сообщений чата {}", chatId);
    try {
      String hql = "FROM ChatMessage m WHERE m.chat.id = :chatId ORDER BY m.createdAt DESC";
      return sessionFactory.getCurrentSession()
          .createQuery(hql, ChatMessage.class)
          .setParameter("chatId", chatId)
          .getResultList();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске сообщений чата", e);
    }
  }
}