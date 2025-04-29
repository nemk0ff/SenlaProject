package ru.senla.socialnetwork.dao.communities.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.dao.communities.CommunityDao;
import ru.senla.socialnetwork.model.communities.Community;

@Repository
@Slf4j
public class CommunityDaoImpl extends HibernateAbstractDao<Community> implements CommunityDao {
  protected CommunityDaoImpl(SessionFactory sessionFactory) {
    super(Community.class, sessionFactory);
  }

}
