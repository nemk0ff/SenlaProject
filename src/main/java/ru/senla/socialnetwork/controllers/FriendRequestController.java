package ru.senla.socialnetwork.controllers;

import java.security.Principal;
import org.springframework.http.ResponseEntity;

public interface FriendRequestController {
  ResponseEntity<?> getAll();

  ResponseEntity<?> showFriends(Principal principal);
}
