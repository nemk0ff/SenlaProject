package ru.senla.socialnetwork.dao.chats;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.MemberRole;

public interface ChatMemberDao extends GenericDao<ChatMember> {
  void saveAll(List<ChatMember> members);

  List<ChatMember> findAllByChatId(Long chatId);

  Optional<ChatMember> findByChatIdAndUserEmail(Long chatId, String userEmail);

  Optional<ChatMember> findActiveByChatIdAndUserEmail(Long chatId, String userEmail);

  long countByChatIdAndRole(Long chatId, MemberRole role);

  long countByChatId(Long chatId);
}
