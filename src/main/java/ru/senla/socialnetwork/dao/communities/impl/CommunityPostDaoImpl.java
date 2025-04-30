package ru.senla.socialnetwork.dao.communities.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.dao.communities.CommunityPostDao;
import ru.senla.socialnetwork.model.communities.CommunityPost;

@Repository
@Slf4j
public class CommunityPostDaoImpl extends HibernateAbstractDao<CommunityPost> implements CommunityPostDao {
  protected CommunityPostDaoImpl(SessionFactory sessionFactory) {
    super(CommunityPost.class, sessionFactory);
  }

  @Override
  public List<CommunityPost> findAllByCommunity(Long communityId) {
    return sessionFactory.getCurrentSession()
        .createQuery("FROM CommunityPost cp WHERE cp.author_id.community.id = :communityId",
            CommunityPost.class)
        .setParameter("communityId", communityId)
        .getResultList();
  }
}
