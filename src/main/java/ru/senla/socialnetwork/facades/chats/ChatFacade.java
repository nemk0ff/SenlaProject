package ru.senla.socialnetwork.facades.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;

public interface ChatFacade {
  List<ChatDTO> getUserChats(String email);

  ChatDTO create(CreateGroupChatDTO chatDTO, String creatorEmail);

  ChatDTO create(String creator, String participant);

  void delete(Long chatId, String clientEmail);

  ChatDTO get(Long chatId, String clientEmail);
}
