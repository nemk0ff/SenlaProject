package ru.senla.socialnetwork.model.entities;

public sealed interface MyEntity permits User {
  Long getId();
  void setId(Long id);
}
