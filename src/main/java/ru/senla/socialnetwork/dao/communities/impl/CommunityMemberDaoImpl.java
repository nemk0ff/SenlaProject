package ru.senla.socialnetwork.dao.communities.impl;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.dao.communities.CommunityMemberDao;
import ru.senla.socialnetwork.model.communities.CommunityMember;

@Repository
@Slf4j
public class CommunityMemberDaoImpl extends HibernateAbstractDao<CommunityMember> implements CommunityMemberDao {
  protected CommunityMemberDaoImpl(SessionFactory sessionFactory) {
    super(CommunityMember.class, sessionFactory);
  }

  @Override
  public Optional<CommunityMember> findByCommunityAndUser(Long communityId, String userEmail) {
    log.debug("Поиск участника сообщества {} для пользователя {}", communityId, userEmail);
    try {
      return sessionFactory.getCurrentSession()
          .createQuery("FROM CommunityMember cm WHERE cm.community.id = :communityId " +
                  "AND lower(cm.user.email) = lower(:userEmail)",
              CommunityMember.class)
          .setParameter("communityId", communityId)
          .setParameter("userEmail", userEmail)
          .uniqueResultOptional();
    } catch (HibernateException e) {
      log.error("Ошибка при поиске участника сообщества: {}", e.getMessage());
      throw new DataRetrievalFailureException("Ошибка при поиске участника сообщества", e);
    }
  }

  @Override
  public List<CommunityMember> findAllByCommunity(Long communityId) {
    log.debug("Поиск всех активных участников сообщества {}", communityId);
    try {
      String hql = "FROM CommunityMember cm " +
          "WHERE cm.community.id = :communityId " +
          "AND (cm.leaveDate IS NULL OR cm.joinDate > cm.leaveDate)";

      return sessionFactory.getCurrentSession()
          .createQuery(hql, CommunityMember.class)
          .setParameter("communityId", communityId)
          .getResultList();
    } catch (HibernateException e) {
      log.error("Ошибка при поиске активных участников сообщества: {}", e.getMessage());
      throw new DataRetrievalFailureException(
          "Ошибка при поиске активных участников сообщества", e);
    }
  }
}
