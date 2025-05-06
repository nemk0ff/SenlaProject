package ru.senla.socialnetwork.dao.chats;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.chats.ChatMessage;

public interface ChatMessageDao extends GenericDao<ChatMessage> {
  List<ChatMessage> findByChatId(Long chatId);

  List<ChatMessage> findAnswers(Long chatId, Long messageId);

  List<ChatMessage> findPinnedByChatId(Long chatId);
}
