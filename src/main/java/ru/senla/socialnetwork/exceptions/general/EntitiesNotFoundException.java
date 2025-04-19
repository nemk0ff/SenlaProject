package ru.senla.socialnetwork.exceptions.general;

public class EntitiesNotFoundException extends RuntimeException {
  public EntitiesNotFoundException() {
    super("По вашему запросу ничего не найдено");
  }
}
