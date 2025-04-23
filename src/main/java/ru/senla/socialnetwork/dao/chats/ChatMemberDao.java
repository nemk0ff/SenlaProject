package ru.senla.socialnetwork.dao.chats;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.chats.ChatMember;

public interface ChatMemberDao extends GenericDao<ChatMember> {
  void saveAll(List<ChatMember> members);

  List<ChatMember> findMembersByChatId(Long chatId);
}
