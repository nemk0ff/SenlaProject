package ru.senla.socialnetwork.exceptions.friendRequests;

public class SelfFriendshipException extends FriendRequestException {
  public SelfFriendshipException() {
    super("Нельзя отправить запрос в друзья самому себе");
  }
}
