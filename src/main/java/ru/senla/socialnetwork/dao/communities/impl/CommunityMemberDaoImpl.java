package ru.senla.socialnetwork.dao.communities.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
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
}
