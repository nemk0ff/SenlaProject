package ru.senla.socialnetwork.repository;

import java.util.Optional;
import ru.senla.socialnetwork.model.entities.MyEntity;

public interface GenericDao<T extends MyEntity> {
  T update(T entity);

  Optional<T> find(Long id);
}