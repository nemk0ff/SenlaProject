package ru.senla.socialnetwork.services;

import java.time.LocalDate;
import ru.senla.socialnetwork.model.users.Gender;
import ru.senla.socialnetwork.model.users.ProfileType;
import ru.senla.socialnetwork.model.users.UserRole;

public interface TestConstants {
  long TEST_USER_ID = 1L;
  UserRole TEST_ROLE = UserRole.USER;
  String TEST_EMAIL = "test_email@senla.ru";
  String TEST_NEW_EMAIL = "test_new@senla.ru";
  String TEST_PASSWORD = "test_password";
  String TEST_ENCODED_PASSWORD = "encoded_password";
  String TEST_NAME = "Test Senla User";
  String TEST_SURNAME = "Test Surname";
  Gender TEST_GENDER = Gender.MALE;
  String TEST_ABOUT_ME = "Test about me";
  ProfileType TEST_PROFILE_TYPE = ProfileType.OPEN;
  LocalDate TEST_BIRTHDATE = LocalDate.of(2000, 1, 1);
}