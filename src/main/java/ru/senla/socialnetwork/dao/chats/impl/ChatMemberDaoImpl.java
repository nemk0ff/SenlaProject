package ru.senla.socialnetwork.dao.chats.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.dao.impl.HibernateAbstractDao;
import ru.senla.socialnetwork.model.chats.ChatMember;

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
}
