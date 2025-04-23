package ru.senla.socialnetwork.services.chats.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateChatDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMapper;
import ru.senla.socialnetwork.exceptions.chats.InvalidChatException;
import ru.senla.socialnetwork.exceptions.users.UserNotRegisteredException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatService;
import ru.senla.socialnetwork.services.general.CommonService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final CommonService commonService;
  private final ChatDao chatDao;
  private final ChatMemberDao chatMemberDao;

  @Override
  @Transactional
  public ChatDTO create(CreateChatDTO request) {
    validateChat(request);

    Chat savedChat = saveChat(request);

    addMembersToChat(savedChat, request);

    return ChatMapper.INSTANCE.chatToChatDTO(savedChat);
  }

  @Override
  @Transactional
  public void deleteChat(Long chatId) {
    Chat chat = chatDao.find(chatId)
        .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));

    List<ChatMember> members = chatMemberDao.findMembersByChatId(chatId);
    members.forEach(chatMemberDao::delete);

    chatDao.delete(chat);
  }

  private void validateChat(CreateChatDTO chatDTO) {
    if(!commonService.existsByEmail(chatDTO.creatorEmail())) {
      throw new UserNotRegisteredException(chatDTO.creatorEmail());
    }

    if (!chatDTO.isGroup()) {
      if (chatDTO.membersEmails().size() != 1) {
        throw new InvalidChatException("Личный чат должен иметь ровно одного участника");
      } else if (chatDao.existsByMembers(chatDTO.creatorEmail(),
          chatDTO.membersEmails().iterator().next())) {
        throw new InvalidChatException("У вас уже есть личный чат с ");
      }
    }
    if (chatDTO.isGroup() && chatDTO.membersEmails().isEmpty()) {
      throw new InvalidChatException("Групповой чат должен иметь хотя бы одного участника");
    }
  }

  private Chat saveChat(CreateChatDTO chatDTO) {
    return chatDao.saveOrUpdate(Chat.builder()
        .name(chatDTO.name())
        .isGroup(chatDTO.isGroup())
        .createdAt(ZonedDateTime.now())
        .build());
  }

  private void addMembersToChat(Chat chat, CreateChatDTO chatDTO) {
    Set<String> membersEmails = chatDTO.membersEmails();
    membersEmails.add(chatDTO.creatorEmail());

    List<ChatMember> chatMembers = new ArrayList<>();

    for (String memberEmail : chatDTO.membersEmails()) {
      User user = commonService.getUserByEmail(memberEmail);
      chatMembers.add(ChatMember.builder()
          .chat(chat)
          .user(user)
          .role(chatDTO.creatorEmail().equals(memberEmail) ?
              MemberRole.ADMIN : MemberRole.MEMBER)
          .joinDate(ZonedDateTime.now())
          .build());
    }
    chatMemberDao.saveAll(chatMembers);
  }
}
