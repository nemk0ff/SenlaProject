package ru.senla.socialnetwork.facades.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;

public interface ChatFacade {
  List<ChatDTO> getUserChats(String email);

  ChatDTO create(CreateGroupChatDTO chatDTO);

  ChatDTO create(CreatePersonalChatDTO chatDTO);

  void delete(Long chatId);

  ChatDTO get(Long chatId);
}
