package ru.senla.socialnetwork.services.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.model.chats.Chat;

public interface ChatService {
  List<Chat> getAllByUser(Long userId);

  Chat create(CreateGroupChatDTO chatDTO);

  Chat create(String firstEmail, String secondEmail, String chatName);

  void delete(Chat chat);

  Chat get(Long chatId);
}
