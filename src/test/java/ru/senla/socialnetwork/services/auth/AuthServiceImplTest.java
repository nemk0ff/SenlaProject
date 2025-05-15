package ru.senla.socialnetwork.services.auth;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.senla.socialnetwork.dao.users.UserDao;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.AuthResponseDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.exceptions.auth.IllegalPasswordException;
import ru.senla.socialnetwork.exceptions.auth.UserNotRegisteredException;
import ru.senla.socialnetwork.exceptions.users.EmailAlreadyExistsException;
import ru.senla.socialnetwork.model.users.User;
import static ru.senla.socialnetwork.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
  @Mock
  private UserDao userDao;
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthServiceImpl authService;

  private User testUser;
  private AuthRequestDTO authRequest;
  private RegisterDTO registerDTO;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .password(TEST_ENCODED_PASSWORD)
        .name(TEST_NAME)
        .role(TEST_ROLE)
        .build();

    authRequest = new AuthRequestDTO(TEST_EMAIL_1, TEST_PASSWORD);
    registerDTO = new RegisterDTO(
        TEST_EMAIL_1, TEST_PASSWORD, TEST_NAME, TEST_SURNAME,
        TEST_BIRTHDATE, TEST_GENDER, TEST_ABOUT_ME, TEST_PROFILE_TYPE);
  }

  @Nested
  class GetAuthResponseTests {
    @Test
    void getAuthResponse_whenValidCredentials_thenReturnAuthResponse() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));
      when(passwordEncoder.matches(TEST_PASSWORD, TEST_ENCODED_PASSWORD)).thenReturn(true);

      AuthResponseDTO response = authService.getAuthResponse(authRequest);

      assertThat(response.role()).isEqualTo("ROLE_USER");
      assertThat(response.token()).isNotBlank();
      verify(userDao).findByEmail(TEST_EMAIL_1);
      verify(passwordEncoder).matches(TEST_PASSWORD, TEST_ENCODED_PASSWORD);
    }

    @Test
    void getAuthResponse_whenInvalidPassword_thenThrowIllegalPasswordException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));
      when(passwordEncoder.matches(TEST_PASSWORD, TEST_ENCODED_PASSWORD)).thenReturn(false);

      assertThatThrownBy(() -> authService.getAuthResponse(authRequest))
          .isInstanceOf(IllegalPasswordException.class);
    }

    @Test
    void getAuthResponse_whenUserNotFound_thenThrowUserNotRegisteredException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> authService.getAuthResponse(authRequest))
          .isInstanceOf(UserNotRegisteredException.class)
          .hasMessageContaining(TEST_EMAIL_1);
    }
  }

  @Nested
  class RegisterTests {
    @Test
    void register_whenNewUser_thenReturnUserResponse() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());
      when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
      when(userDao.saveOrUpdate(any(User.class))).thenReturn(testUser);

      UserResponseDTO response = authService.register(registerDTO);

      assertThat(response.email()).isEqualTo(TEST_EMAIL_1);
      assertThat(response.name()).isEqualTo(TEST_NAME);
      verify(userDao).findByEmail(TEST_EMAIL_1);
      verify(passwordEncoder).encode(TEST_PASSWORD);
      verify(userDao).saveOrUpdate(any(User.class));
    }

    @Test
    void register_whenEmailExists_thenThrowEmailAlreadyExistsException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));

      assertThatThrownBy(() -> authService.register(registerDTO))
          .isInstanceOf(EmailAlreadyExistsException.class)
          .hasMessageContaining(TEST_EMAIL_1);
    }
  }

  @Nested
  class LoadUserByUsernameTests {
    @Test
    void loadUserByUsername_whenUserExists_thenReturnUserDetails() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.of(testUser));

      UserDetails userDetails = authService.loadUserByUsername(TEST_EMAIL_1);

      assertThat(userDetails.getUsername()).isEqualTo(TEST_EMAIL_1);
      assertThat(userDetails.getPassword()).isEqualTo(TEST_ENCODED_PASSWORD);
      assertThat(userDetails.getAuthorities())
          .extracting("authority")
          .containsExactly("ROLE_USER");
      verify(userDao).findByEmail(TEST_EMAIL_1);
    }

    @Test
    void loadUserByUsername_whenUserNotExists_thenThrowUserNotRegisteredException() {
      when(userDao.findByEmail(TEST_EMAIL_1)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> authService.loadUserByUsername(TEST_EMAIL_1))
          .isInstanceOf(UserNotRegisteredException.class)
          .hasMessageContaining(TEST_EMAIL_1);
    }
  }
}