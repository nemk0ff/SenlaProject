package ru.senla.socialnetwork.dao.communities.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
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

  @Override
  public List<Community> getAll() {
    log.info("Получение списка всех сообществ...");
    try {
      List<Community> communities = sessionFactory.getCurrentSession()
          .createNamedQuery("Community.findAll", Community.class)
          .list();

      log.info("Найдено {} сообществ", communities.size());
      return communities;
    } catch (Exception e) {
      throw new DataRetrievalFailureException(
          "Ошибка при получении списка всех сообществ", e);
    }
  }
}
