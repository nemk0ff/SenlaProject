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
  public Optional<CommunityMember> findByCommunityIdAndUserEmail(Long communityId, String userEmail) {
    log.debug("Поиск участника сообщества {} для пользователя {}", communityId, userEmail);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("CommunityMember.findByCommunityIdAndUserEmail", CommunityMember.class)
          .setParameter("communityId", communityId)
          .setParameter("userEmail", userEmail)
          .uniqueResultOptional();
    } catch (HibernateException e) {
      log.error("Ошибка при поиске участника сообщества: {}", e.getMessage());
      throw new DataRetrievalFailureException("Ошибка при поиске участника сообщества", e);
    }
  }

  @Override
  public List<CommunityMember> findAllByCommunityId(Long communityId) {
    log.debug("Поиск всех активных участников сообщества {}", communityId);
    try {
      return sessionFactory.getCurrentSession()
          .createNamedQuery("CommunityMember.findAllByCommunityId", CommunityMember.class)
          .setParameter("communityId", communityId)
          .getResultList();
    } catch (HibernateException e) {
      log.error("Ошибка при поиске активных участников сообщества: {}", e.getMessage());
      throw new DataRetrievalFailureException(
          "Ошибка при поиске активных участников сообщества", e);
    }
  }

  @Override
  public List<CommunityMember> findAllByUserId(Long userId) {
    log.info("Получение списка всех участников сообществ, " +
            "которыми является пользователь id={}...", userId);
    try {
      List<CommunityMember> communities = sessionFactory.getCurrentSession()
          .createNamedQuery("CommunityMember.findAllByUserId", CommunityMember.class)
          .setParameter("userId", userId)
          .getResultList();

      log.info("Найдено {} сообществ, на которые подписан пользователь id={}",
          communities.size(), userId);
      return communities;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех сообществ пользователя id=" + userId, e);
    }
  }
}
