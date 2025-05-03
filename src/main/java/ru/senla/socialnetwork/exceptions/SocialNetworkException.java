package ru.senla.socialnetwork.exceptions;

import lombok.Getter;

@Getter
public class SocialNetworkException extends RuntimeException {
  private final String action;

  public SocialNetworkException(String message, String action) {
    super(message);
    this.action = action;
  }

  public SocialNetworkException(String message) {
    super(message);
    action = "В программе возникла ошибка";
  }
}
