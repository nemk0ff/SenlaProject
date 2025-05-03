package ru.senla.socialnetwork.facades.chats.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.chats.CreatePersonalChatDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMapper;
import ru.senla.socialnetwork.facades.chats.ChatFacade;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.ChatService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatFacadeImpl implements ChatFacade {
  private final ChatMapper chatMapper;
  private final ChatService chatService;
  private final ChatMemberService chatMemberService;
  private final UserService userService;

  @Override
  public ChatDTO create(CreateGroupChatDTO chatDTO) {
    Chat chat = chatService.create(chatDTO);

    addMembersToChat(chat, chatDTO);

    return get(chat.getId());
  }

  @Override
  public ChatDTO create(CreatePersonalChatDTO chatDTO) {
    User creator = userService.getUserByEmail(chatDTO.creatorEmail());
    User friend = userService.getUserByEmail(chatDTO.friendEmail());
    String chatName = chatDTO.creatorEmail() + " - " + chatDTO.friendEmail();

    // Создаём чат
    Chat chat = chatService.create(chatDTO, chatName);

    // Создаём участников
    List<ChatMember> chatMembers = new ArrayList<>();
    chatMembers.add(createChatMember(chat, creator, MemberRole.ADMIN));
    chatMembers.add(createChatMember(chat, friend, MemberRole.ADMIN));

    // Созраняем участников в бд
    chatMemberService.saveMembers(chatMembers);
    // Добавляем в чат созданных участников
    chat.getMembers().addAll(chatMembers);

    return get(chat.getId());
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
      User user = userService.getUserByEmail(memberEmail);
      ChatMember member = createChatMember(chat, user,
          request.creatorEmail().equals(memberEmail) ?
              MemberRole.ADMIN : MemberRole.MEMBER);
      chatMembers.add(member);
    }
    chatMemberService.saveMembers(chatMembers);
    chat.getMembers().addAll(chatMembers);
  }

  @Override
  public void delete(Long chatId) {
    Chat chat = chatService.get(chatId);

    List<ChatMember> members = chatMemberService.getMembers(chatId);
    members.forEach(chatMemberService::removeMember);

    chatService.delete(chat);
  }

  @Override
  public ChatDTO get(Long chatId) {
    return chatMapper.chatToChatDTO(chatService.get(chatId));
  }
}
