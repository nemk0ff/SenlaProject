package ru.senla.socialnetwork.dao;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataRetrievalFailureException;
import ru.senla.socialnetwork.model.MyEntity;

@Slf4j
public abstract class HibernateAbstractDao<T extends MyEntity> implements GenericDao<T> {
  private final Class<T> type;
  protected final SessionFactory sessionFactory;

  protected HibernateAbstractDao(Class<T> type, SessionFactory sessionFactory) {
    this.type = type;
    this.sessionFactory = sessionFactory;
  }

  @Override
  public T saveOrUpdate(T entity) {
    log.debug("Сохранение/обновление: {}", entity);
    try {
      T mergedEntity = sessionFactory.getCurrentSession().merge(entity);
      log.info("Успешно сохранено/обновлено: {}", mergedEntity);
      return mergedEntity;
    } catch (HibernateException e) {
      throw new DataRetrievalFailureException("Ошибка при сохранении/обновлении " + entity, e);
    }
  }

  @Override
  public Optional<T> find(Long id) {
    log.debug("Поиск {} [{}]...", type.getSimpleName(), id);
    try {
      Optional<T> entity = Optional.ofNullable(sessionFactory.getCurrentSession().get(type, id));
      log.debug(entity.isPresent() ? "{} с id {} найден: {}" : "{} с id {} не найден",
          type.getSimpleName(), id, entity.orElse(null));
      return entity;
    } catch (HibernateException e) {
      log.error("Ошибка при поиске {} с id {}: {}", type.getSimpleName(), id, e.getMessage());
      throw new DataRetrievalFailureException("Ошибка при поиске " + type.getSimpleName(), e);
    }
  }

  @Override
  public void delete(T entity) {
    log.debug("Удаляем из базы : {}", entity);
    sessionFactory.getCurrentSession().remove(entity);
    log.debug("Удаление прошло успешно: {}", entity.getId());
  }
}