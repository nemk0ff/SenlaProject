package ru.senla.socialnetwork.services.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.model.entities.FriendRequest;
import ru.senla.socialnetwork.model.entities.User;
import ru.senla.socialnetwork.repository.FriendRequestDao;
import ru.senla.socialnetwork.repository.UserDao;
import ru.senla.socialnetwork.repository.impl.UserDaoImpl;
import ru.senla.socialnetwork.services.FriendRequestService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
  private final UserDao userDao;
  private final FriendRequestDao friendRequestDao;

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getAll() {
    return friendRequestDao.getAll();
  }

  @Transactional(readOnly = true)
  @Override
  public List<FriendRequest> getByUserEmail(String userEmail) {
    User user = userDao.findByEmail(userEmail).orElseThrow(() ->
        new EntityNotFoundException("Пользователь " + userEmail + " не зарегистрирован."));
    return friendRequestDao.getByUserId(user.getId());
  }
}
