package ru.senla.socialnetwork.dao.chats;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.chats.Chat;

public interface ChatDao extends GenericDao<Chat> {
  boolean existsByMembers(String email1, String email2);

  Optional<Chat> findWithMembersAndUsers(Long chatId);

  List<Chat> findAllActiveByUserId(Long userId);
}
