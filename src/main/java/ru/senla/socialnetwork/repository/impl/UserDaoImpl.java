package ru.senla.socialnetwork.repository.impl;

import java.util.Optional;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.repository.UserDao;

@Repository
@Transactional
public class UserDaoImpl extends HibernateAbstractDao<User> implements UserDao {

  public UserDaoImpl(SessionFactory sessionFactory) {
    super(User.class, sessionFactory);
  }

  @Override
  public Optional<User> get(long userId) {
    return find(userId);
  }
}
