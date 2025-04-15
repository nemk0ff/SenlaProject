package ru.senla.socialnetwork.controllers.impl;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

public class ControllersUtils {
  public static void checkAccessDenied(Authentication authentication, String errorMessage,
                                       String requiredUsername) {
    if (authentication.getAuthorities().stream()
        .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
        && !requiredUsername.equals(authentication.getName())) {
      throw new AccessDeniedException(errorMessage);
    }
  }
}