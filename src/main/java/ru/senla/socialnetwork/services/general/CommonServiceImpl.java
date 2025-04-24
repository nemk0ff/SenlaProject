package ru.senla.socialnetwork.services.general;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.impl.UserDaoImpl;
import ru.senla.socialnetwork.exceptions.users.UserNotRegisteredException;
import ru.senla.socialnetwork.model.users.User;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommonServiceImpl implements CommonService {
  private final UserDaoImpl userDao;

  @Override
  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    return userDao.findByEmail(email).orElseThrow(
        () -> new UserNotRegisteredException(email));
  }

  @Transactional
  @Override
  public boolean existsByEmail(String email) {
    return userDao.findByEmail(email).isPresent();
  }

}
