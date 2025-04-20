package ru.senla.socialnetwork.exceptions.friendRequests;

public class AlreadySentException extends FriendRequestException {
  public AlreadySentException(String email) {
    super("Вы уже отправили заявку в друзья пользователю " + email);
  }
}
