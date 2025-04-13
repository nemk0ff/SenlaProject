package ru.senla.socialnetwork.services;

import ru.senla.socialnetwork.model.entities.User;

public interface UserService {
  User get(long userId);

  // TODO: Редактирование персональной информации пользователя

  // TODO: Поиск пользователей по: фамилии, имени, полу, возрасту
}
