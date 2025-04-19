package ru.senla.socialnetwork.exceptions.friendRequests;

public class AlreadyFriendsException extends FriendRequestException {
  public AlreadyFriendsException(String email) {
    super(email + " уже ваш друг");
  }
}
