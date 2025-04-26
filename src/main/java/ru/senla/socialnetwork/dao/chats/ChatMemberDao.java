package ru.senla.socialnetwork.dao.chats;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface ChatMemberDao extends GenericDao<ChatMember> {
  void saveAll(List<ChatMember> members);

  List<ChatMember> findMembersByChatId(Long chatId);

  Optional<ChatMember> findByChatIdAndUserEmail(Long chatId, String userEmail);

  boolean existsByChatIdAndUserEmail(Long chatId, String userEmail);

  long countByChatIdAndRole(Long chatId, MemberRole role);

  long countByChatId(Long chatId);
}
