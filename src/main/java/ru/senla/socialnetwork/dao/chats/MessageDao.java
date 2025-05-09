package ru.senla.socialnetwork.dao.chats;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.chats.Message;

public interface MessageDao extends GenericDao<Message> {
  List<Message> findByChatId(Long chatId);

  List<Message> findAnswers(Long chatId, Long messageId);

  List<Message> findPinnedByChatId(Long chatId);
}
