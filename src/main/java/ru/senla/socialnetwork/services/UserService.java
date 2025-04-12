package ru.senla.socialnetwork.services;

import ru.senla.socialnetwork.model.entities.User;

public interface UserService {
  User get(long userId);
}
