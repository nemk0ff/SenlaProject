package ru.senla.socialnetwork.exceptions.communities;

public class CommunityMemberException extends CommunityException {
  public CommunityMemberException(String message) {
    super(message, "Ошибка при действии с участником сообщества");
  }
}
