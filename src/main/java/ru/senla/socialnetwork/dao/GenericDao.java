package ru.senla.socialnetwork.dao;

import java.util.Optional;
import ru.senla.socialnetwork.model.MyEntity;

public interface GenericDao<T extends MyEntity> {
  T saveOrUpdate(T entity);

  Optional<T> find(Long id);

  void delete(T entity);
}