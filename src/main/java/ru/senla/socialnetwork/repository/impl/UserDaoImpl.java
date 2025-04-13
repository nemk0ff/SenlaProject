package ru.senla.socialnetwork.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.repository.UserDao;

@Repository
@Transactional
@Slf4j
public class UserDaoImpl extends HibernateAbstractDao<User> implements UserDao {

  public UserDaoImpl(SessionFactory sessionFactory) {
    super(User.class, sessionFactory);
  }

  @Override
  public List<User> findByParam(User user) {
    log.info("Поиск пользователя {}...", user);
    try {
      StringBuilder hql = new StringBuilder("FROM User WHERE 1=1");
      Map<String, Object> params = new HashMap<>();

      if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
        hql.append(" AND firstName = :firstName");
        params.put("firstName", user.getFirstName());
      }
      if (user.getLastName() != null && !user.getLastName().isEmpty()) {
        hql.append(" AND lastName = :lastName");
        params.put("lastName", user.getLastName());
      }
      if (user.getGender() != null) {
        hql.append(" AND gender = :gender");
        params.put("gender", user.getGender());
      }
      if (user.getBirthDate() != null) {
        hql.append(" AND birthDate = :birthDate");
        params.put("birthDate", user.getBirthDate());
      }

      Query<User> query = sessionFactory.getCurrentSession()
          .createQuery(hql.toString(), User.class);
      params.forEach(query::setParameter);

      List<User> users = query.list();
      log.info("Найдено {} пользователей", users.size());
      return users;
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске пользователей", e);
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    try {
      return Optional.ofNullable(
          sessionFactory.getCurrentSession()
              .createQuery("FROM User WHERE mail = :mail", User.class)
              .setParameter("mail", email)
              .uniqueResult()
      );
    } catch (HibernateException e) {
      throw new DataRetrievalFailureException("Ошибка при поиске пользователя " + email, e);
    }
  }
}
