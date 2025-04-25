package ru.senla.socialnetwork.services.chats.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
import ru.senla.socialnetwork.exceptions.users.UserNotRegisteredException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatService;
import ru.senla.socialnetwork.services.chats.CommonChatService;
import ru.senla.socialnetwork.services.common.CommonService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final CommonService commonService;
  private final CommonChatService commonChatService;
  private final ChatMapper chatMapper;
  private final ChatDao chatDao;
  private final ChatMemberDao chatMemberDao;

  @Override
  @Transactional
  public ChatDTO create(CreateGroupChatDTO request) {
    validateEmail(request.creatorEmail());
    if (request.membersEmails().isEmpty()) {
      throw new ChatException("Групповой чат должен иметь хотя бы одного участника");
    }

    Chat chat = chatDao.saveOrUpdate(Chat.builder()
        .name(request.name())
        .isGroup(true)
        .createdAt(ZonedDateTime.now())
        .build());
    addMembersToChat(chat, request);

    log.info("Создан групповой чат {} пользователем {}", request.name(), request.creatorEmail());
    return getChat(chat.getId());
  }

  @Override
  @Transactional
  public ChatDTO create(CreatePersonalChatDTO request) {
    User creator = commonService.getUserByEmail(request.creatorEmail());
    User friend = commonService.getUserByEmail(request.friendEmail());

    String chatName = request.creatorEmail() + " - " + request.friendEmail();
    if (chatDao.existsByMembers(request.creatorEmail(), request.friendEmail())) {
      throw new ChatException("Личный чат " + chatName + " уже существует");
    }

    Chat chat = chatDao.saveOrUpdate(Chat.builder()
        .name(chatName)
        .isGroup(false)
        .createdAt(ZonedDateTime.now())
        .build());

    List<ChatMember> chatMembers = new ArrayList<>();
    chatMembers.add(createChatMember(chat, creator, MemberRole.ADMIN));
    chatMembers.add(createChatMember(chat, friend, MemberRole.ADMIN));

    chatMemberDao.saveAll(chatMembers);
    chat.getMembers().addAll(chatMembers);

    log.info("Создан личный чат между {} и {}", creator.getEmail(), friend.getEmail());
    return getChat(chat.getId());
  }

  @Override
  @Transactional
  public void deleteChat(Long chatId) {
    Chat chat = commonChatService.getChat(chatId);

    List<ChatMember> members = chatMemberDao.findMembersByChatId(chatId);
    members.forEach(chatMemberDao::delete);

    chatDao.delete(chat);
  }

  @Override
  @Transactional(readOnly = true)
  public ChatDTO getChat(Long chatId) {
    return chatMapper.chatToChatDTO(commonChatService.getChat(chatId));
  }

  private void validateEmail(String userEmail) {
    if(!commonService.existsByEmail(userEmail)) {
      throw new UserNotRegisteredException(userEmail);
    }
  }

  private ChatMember createChatMember(Chat chat, User user, MemberRole role) {
    return ChatMember.builder()
        .chat(chat)
        .user(user)
        .role(role)
        .joinDate(ZonedDateTime.now())
        .build();
  }

  private void addMembersToChat(Chat chat, CreateGroupChatDTO request) {
    Set<String> allMembers = new HashSet<>(request.membersEmails());
    allMembers.add(request.creatorEmail());

    List<ChatMember> chatMembers = new ArrayList<>();

    for (String memberEmail : allMembers) {
      User user = commonService.getUserByEmail(memberEmail);
      ChatMember member = createChatMember(chat, user,
          request.creatorEmail().equals(memberEmail) ?
              MemberRole.ADMIN : MemberRole.MEMBER);
      chatMembers.add(member);
    }
    chatMemberDao.saveAll(chatMembers);
    chat.getMembers().addAll(chatMembers);
  }
}
