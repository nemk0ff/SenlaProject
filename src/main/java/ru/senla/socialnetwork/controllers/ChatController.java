package ru.senla.socialnetwork.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateChatDTO;

public interface ChatController {
  ResponseEntity<ChatDTO> createChat(@Valid CreateChatDTO request);
}
