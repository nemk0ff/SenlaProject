package ru.senla.socialnetwork.services.users;


import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.socialnetwork.dao.users.UserDao;
import ru.senla.socialnetwork.dto.users.UserRequestDTO;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.exceptions.users.UserException;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.UserRole;
import static ru.senla.socialnetwork.TestConstants.*;
import ru.senla.socialnetwork.services.user.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  @Mock
  private UserDao userDao;

  @InjectMocks
  private UserServiceImpl userService;

  private User testUser;
  private UserRequestDTO editDTO;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID)
        .email(TEST_EMAIL_1)
        .name(TEST_NAME)
        .surname(TEST_SURNAME)
        .gender(TEST_GENDER)
        .birthDate(TEST_BIRTHDATE)
        .role(TEST_ROLE)
        .build();

    editDTO = new UserRequestDTO(
        TEST_EMAIL_2, TEST_SURNAME, TEST_BIRTHDATE,
        TEST_GENDER, TEST_PROFILE_TYPE, TEST_ABOUT_ME);
  }

  @Nested
  class GetTests {
    @Test
    void get_whenUserExists_thenReturnUser() {
      when(userDao.find(TEST_USER_ID)).thenReturn(Optional.of(testUser));

      User result = userService.get(TEST_USER_ID);

      assertThat(result).isEqualTo(testUser);
      verify(userDao).find(TEST_USER_ID);
    }

    @Test
    void get_whenUserNotExists_thenThrowException() {
      when(userDao.find(TEST_USER_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.get(TEST_USER_ID))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("id" + TEST_USER_ID);
    }
  }

  @Nested
  class FindTests {
    @Test
    void find_whenParamsProvided_thenReturnUsers() {
      List<User> expectedUsers = List.of(testUser);
      when(userDao.findByParam(TEST_NAME, TEST_SURNAME, TEST_GENDER, TEST_BIRTHDATE))
          .thenReturn(expectedUsers);

      List<User> result = userService.find(TEST_NAME, TEST_SURNAME, TEST_GENDER, TEST_BIRTHDATE);

      assertThat(result).isEqualTo(expectedUsers);
      verify(userDao).findByParam(TEST_NAME, TEST_SURNAME, TEST_GENDER, TEST_BIRTHDATE);
    }

    @Test
    void find_whenNullParams_thenCallDaoWithNulls() {
      List<User> expectedUsers = List.of(testUser);
      when(userDao.findByParam(null, null, null, null))
          .thenReturn(expectedUsers);

      List<User> result = userService.find(null, null, null, null);

      assertThat(result).isEqualTo(expectedUsers);
      verify(userDao).findByParam(null, null, null, null);
    }
  }

  @Nested
  class EditTests {
    @Test
    void edit_whenValidData_thenUpdateAndReturnUser() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));
      when(userDao.saveOrUpdate(any(User.class))).thenReturn(testUser);

      User result = userService.edit(editDTO, TEST_EMAIL_1);

      assertThat(result.getName()).isEqualTo(editDTO.name());
      assertThat(result.getSurname()).isEqualTo(editDTO.surname());
      assertThat(result.getGender()).isEqualTo(editDTO.gender());
      assertThat(result.getBirthDate()).isEqualTo(editDTO.birthDate());
      assertThat(result.getProfileType()).isEqualTo(editDTO.profileType());
      assertThat(result.getAboutMe()).isEqualTo(editDTO.aboutMe());
      verify(userDao).findByEmail(TEST_EMAIL_1);
      verify(userDao).saveOrUpdate(testUser);
    }

    @Test
    void edit_whenPartialData_thenUpdateOnlyProvidedFields() {
      UserRequestDTO partialEditDTO = new UserRequestDTO(
          null,
          "New Surname",
          null,
          null,
          null,
          null
      );

      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));
      when(userDao.saveOrUpdate(any(User.class))).thenReturn(testUser);

      User result = userService.edit(partialEditDTO, TEST_EMAIL_1);

      assertThat(result.getName()).isEqualTo(testUser.getName());
      assertThat(result.getSurname()).isEqualTo(partialEditDTO.surname());
      verify(userDao).findByEmail(TEST_EMAIL_1);
      verify(userDao).saveOrUpdate(testUser);
    }

    @Test
    void edit_whenUserNotExists_thenThrowException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.edit(editDTO, TEST_EMAIL_1))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining(TEST_EMAIL_1);
    }
  }

  @Nested
  class ChangeEmailTests {
    @Test
    void changeEmail_whenValidNewEmail_thenUpdateEmail() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));
      when(userDao.findByEmail(TEST_EMAIL_2)).thenReturn(Optional.empty());
      when(userDao.saveOrUpdate(any(User.class))).thenReturn(testUser);

      User result = userService.changeEmail(TEST_EMAIL_1, TEST_EMAIL_2);

      assertThat(result.getEmail()).isEqualTo(TEST_EMAIL_2);
      verify(userDao).findByEmail(TEST_EMAIL_1);
      verify(userDao).findByEmail(TEST_EMAIL_2);
      verify(userDao).saveOrUpdate(testUser);
    }

    @Test
    void changeEmail_whenEmailsSame_thenThrowException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));

      assertThatThrownBy(() -> userService.changeEmail(TEST_EMAIL_1, TEST_EMAIL_1))
          .isInstanceOf(UserException.class)
          .hasMessageContaining("Старый и новый email совпадают");
    }

    @Test
    void changeEmail_whenNewEmailExists_thenThrowException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));
      when(userDao.findByEmail(TEST_EMAIL_2)).thenReturn(Optional.of(new User()));

      assertThatThrownBy(() -> userService.changeEmail(TEST_EMAIL_1, TEST_EMAIL_2))
          .isInstanceOf(EmailAlreadyExistsException.class)
          .hasMessageContaining(TEST_EMAIL_2);
    }

    @Test
    void changeEmail_whenUserNotExists_thenThrowException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.changeEmail(TEST_EMAIL_1, TEST_EMAIL_2))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining(TEST_EMAIL_1);
    }
  }

  @Nested
  class GetUserByEmailTests {
    @Test
    void getUserByEmail_whenUserExists_thenReturnUser() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));

      User result = userService.getUserByEmail(TEST_EMAIL_1);

      assertThat(result).isEqualTo(testUser);
      verify(userDao).findByEmail(TEST_EMAIL_1);
    }

    @Test
    void getUserByEmail_whenUserNotExists_thenThrowException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserByEmail(TEST_EMAIL_1))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining(TEST_EMAIL_1);
    }
  }

  @Nested
  class ExistsByEmailTests {
    @Test
    void existsByEmail_whenUserExists_thenReturnTrue() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));

      boolean result = userService.existsByEmail(TEST_EMAIL_1);

      assertThat(result).isTrue();
      verify(userDao).findByEmail(TEST_EMAIL_1);
    }

    @Test
    void existsByEmail_whenUserNotExists_thenReturnFalse() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());

      boolean result = userService.existsByEmail(TEST_EMAIL_1);

      assertThat(result).isFalse();
      verify(userDao).findByEmail(TEST_EMAIL_1);
    }
  }

  @Nested
  class IsAdminTests {
    @Test
    void isAdmin_whenUserIsAdmin_thenReturnTrue() {
      testUser.setRole(UserRole.ADMIN);
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));

      boolean result = userService.isAdmin(TEST_EMAIL_1);

      assertThat(result).isTrue();
      verify(userDao).findByEmail(TEST_EMAIL_1);
    }

    @Test
    void isAdmin_whenUserIsNotAdmin_thenReturnFalse() {
      testUser.setRole(UserRole.USER);
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));

      boolean result = userService.isAdmin(TEST_EMAIL_1);

      assertThat(result).isFalse();
      verify(userDao).findByEmail(TEST_EMAIL_1);
    }

    @Test
    void isAdmin_whenUserNotExists_thenThrowException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.isAdmin(TEST_EMAIL_1))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining(TEST_EMAIL_1);
    }
  }
}