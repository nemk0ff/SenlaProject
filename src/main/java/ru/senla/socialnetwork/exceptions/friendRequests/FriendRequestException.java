package ru.senla.socialnetwork.exceptions.friendRequests;

import ru.senla.socialnetwork.exceptions.SocialNetworkException;

public class FriendRequestException extends SocialNetworkException {
  public FriendRequestException(String message) {
    super(message, "Ошибка при действии с заявками в друзья");
  }
}
