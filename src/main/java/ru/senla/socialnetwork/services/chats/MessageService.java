package ru.senla.socialnetwork.services.chats;

import java.util.List;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.Message;

public interface MessageService {
  Message send(ChatMember member, MessageRequestDTO request);

  List<Message> getAll(Long chatId);

  Message get(Long chatId, Long messageId);

  List<Message> getAnswers(Long chatId, Long messageId);

  List<Message> getPinned(Long chatId);

  Message pin(Long chatId, Long messageId);

  Message unpin(Long chatId, Long messageId);

  void delete(Message message);
}
