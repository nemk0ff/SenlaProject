package ru.senla.socialnetwork.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.repository.impl.UserDaoImpl;
import ru.senla.socialnetwork.services.UserService;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserDaoImpl userDao;

  @Override
  @Transactional(readOnly = true)
  public User get(long userId) {
    return userDao.get(userId).orElseThrow(() -> new EntityNotFoundException(""));
  }
}
