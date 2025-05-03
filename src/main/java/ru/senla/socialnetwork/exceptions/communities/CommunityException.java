package ru.senla.socialnetwork.exceptions.communities;

import lombok.Getter;

@Getter
public class CommunityException extends RuntimeException {
  private final String action;

  public CommunityException(String message, String action) {
    super(message);
    this.action = action;
  }

  public CommunityException(String message) {
    super(message);
    action = "Ошибка при действии с сообществом";
  }
}
