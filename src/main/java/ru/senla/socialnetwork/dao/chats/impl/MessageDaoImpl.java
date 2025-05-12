package ru.senla.socialnetwork.dao.chats.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.chats.MessageDao;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.model.chats.Message;

@Slf4j
@Repository
public class MessageDaoImpl extends HibernateAbstractDao<Message> implements MessageDao {
  protected MessageDaoImpl(SessionFactory sessionFactory) {
    super(Message.class, sessionFactory);
  }

  @Override
  public List<Message> findByChatId(Long chatId) {
    log.info("Поиск сообщений чата {}", chatId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Message.find", Message.class)
          .setParameter("chatId", chatId)
          .getResultList();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске сообщений чата", e);
    }
  }

  @Override
  public List<Message> findAnswers(Long chatId, Long messageId) {
    log.info("Поиск ответов на сообщение {} из чата {}", messageId, chatId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Message.findAnswers", Message.class)
          .setParameter("chatId", chatId)
          .setParameter("messageId", messageId)
          .getResultList();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске ответов на сообщение в чате", e);
    }
  }

  @Override
  public List<Message> findPinnedByChatId(Long chatId) {
    log.info("Поиск закрепленных сообщений чата {}", chatId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Message.findPinnedByChatId", Message.class)
          .setParameter("chatId", chatId)
          .getResultList();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске закрепленных сообщений чата", e);
    }
  }

  @Override
  public Optional<Message> findByIdAndChatId(Long messageId, Long chatId) {
    log.info("Поиск сообщения {} в чате {}", messageId, chatId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("Message.findByIdAndChatId", Message.class)
          .setParameter("chatId", chatId)
          .setParameter("messageId", messageId)
          .uniqueResultOptional();
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске сообщений чата", e);
    }
  }
}