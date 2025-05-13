package ru.senla.socialnetwork.dao.users.impl;

import java.time.LocalDate;
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
import ru.senla.socialnetwork.dao.HibernateAbstractDao;
import ru.senla.socialnetwork.dao.users.UserDao;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.Gender;

@Repository
@Slf4j
public class UserDaoImpl extends HibernateAbstractDao<User> implements UserDao {

  public UserDaoImpl(SessionFactory sessionFactory) {
    super(User.class, sessionFactory);
  }

  @Override
  public List<User> findByParam(String name, String surname, Gender gender, LocalDate birthdate) {
    log.info("Поиск пользователя: {}, {}, {}, {}...", name, surname, gender, birthdate);
    try {
      StringBuilder hql = new StringBuilder("FROM User WHERE 1=1");
      Map<String, Object> params = new HashMap<>();

      if (name != null && !name.isEmpty()) {
        hql.append(" AND name = :name");
        params.put("name", name);
      }
      if (surname != null && !surname.isEmpty()) {
        hql.append(" AND surname = :surname");
        params.put("surname", surname);
      }
      if (gender != null) {
        hql.append(" AND gender = :gender");
        params.put("gender", gender);
      }
      if (birthdate != null) {
        hql.append(" AND birthDate = :birthDate");
        params.put("birthDate", birthdate);
      }

      Query<User> query = sessionFactory.getCurrentSession()
          .createQuery(hql.toString(), User.class);
      params.forEach(query::setParameter);

      List<User> users = query.list();
      log.info("Найдено {} пользователей", users.size());
      return users;
    } catch (Exception e) {
      throw new DataRetrievalFailureException("Ошибка при поиске пользователей по параметрам", e);
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    log.info("Поиск пользователя по email {}", email);
    try {
      Optional<User> user = Optional.ofNullable(
          sessionFactory.getCurrentSession()
              .createNamedQuery("User.findByEmail", User.class)
              .setParameter("email", email)
              .uniqueResult());
      if (user.isPresent()) {
        log.info("Найден пользователь с email {}: id={}", email, user.get().getId());
      } else {
        log.info("Пользователь с email {} не найден", email);
      }
      return user;
    } catch (HibernateException e) {
      throw new DataRetrievalFailureException("Ошибка при поиске пользователя " + email, e);
    }
  }
}
