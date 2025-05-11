package ru.senla.socialnetwork.services.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.senla.socialnetwork.dto.auth.AuthRequestDTO;
import ru.senla.socialnetwork.dto.auth.AuthResponseDTO;
import ru.senla.socialnetwork.dto.auth.RegisterDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;

public interface AuthService extends UserDetailsService {
  AuthResponseDTO getAuthResponse(AuthRequestDTO requestDTO);

  UserResponseDTO register(RegisterDTO registerDTO);
}
