package ru.senla.socialnetwork.dao.users.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.dao.users.WallPostDao;
import ru.senla.socialnetwork.model.users.WallPost;

@Repository
@Slf4j
public class WallPostDaoImpl extends HibernateAbstractDao<WallPost> implements WallPostDao {
  public WallPostDaoImpl(SessionFactory sessionFactory) {
    super(WallPost.class, sessionFactory);
  }

  @Override
  public List<WallPost> findAllByUser(Long userId) {
    return sessionFactory.getCurrentSession()
        .createQuery("FROM WallPost wp WHERE wp.wall_owner.id = :userId",
            WallPost.class)
        .setParameter("userId", userId)
        .getResultList();
  }
}
