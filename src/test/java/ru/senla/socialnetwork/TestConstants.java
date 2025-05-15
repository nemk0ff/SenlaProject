package ru.senla.socialnetwork;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.UserRole;

public interface TestConstants {
  Long TEST_USER_ID_1 = 1L;
  Long TEST_USER_ID_2 = 1L;

  UserRole TEST_ROLE = UserRole.USER;
  String TEST_EMAIL_1 = "test_email@senla.ru";
  String TEST_EMAIL_2 = "test_email2@senla.ru";
  String TEST_PASSWORD = "test_password";
  String TEST_ENCODED_PASSWORD = "encoded_password";
  String TEST_NAME = "Test Senla User";
  String TEST_SURNAME = "Test Surname";
  Gender TEST_GENDER = Gender.MALE;
  String TEST_ABOUT_ME = "Test about me";
  ProfileType TEST_PROFILE_TYPE = ProfileType.OPEN;
  LocalDate TEST_BIRTHDATE = LocalDate.of(2000, 1, 1);

  Long TEST_CHAT_ID = 1L;
  String TEST_CHAT_NAME = "Test Chat";
  ZonedDateTime TEST_DATE = ZonedDateTime.now();
  ZonedDateTime TEST_FUTURE_DATE = TEST_DATE.plusDays(1);

  Long TEST_MESSAGE_ID = 1L;
  Long TEST_REPLY_TO_ID = 2L;
  String TEST_BODY = "Test message body";

  Long TEST_COMMUNITY_ID = 1L;
  String TEST_COMMUNITY_NAME = "Test Community";
  String TEST_BAN_REASON = "Test ban reason";

  Long TEST_POST_ID = 1L;
  String TEST_MOOD = "Test mood";
  String TEST_LOCATION = "Test location";
  Long TEST_COMMENT_ID = 1L;

  Long TEST_REACTION_ID = 1L;
}