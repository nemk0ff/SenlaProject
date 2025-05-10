package ru.senla.socialnetwork.facades.chats.impl;

import jakarta.persistence.EntityNotFoundException;
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
import ru.senla.socialnetwork.dto.mappers.ChatMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
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
  public List<ChatDTO> getUserChats(String email) {
    User user = userService.getUserByEmail(email);
    return chatMapper.toListChatDTO(chatService.getAllByUser(user.getId()));
  }

  @Override
  public ChatDTO create(CreateGroupChatDTO chatDTO, String creatorEmail) {
    Chat chat = chatService.create(chatDTO);

    addMembersToChat(chat, chatDTO, creatorEmail);

    return get(chat.getId(), creatorEmail);
  }

  @Override
  public ChatDTO create(String creatorEmail, String participantEmail) {
    User creator = userService.getUserByEmail(creatorEmail);
    User friend = userService.getUserByEmail(participantEmail);
    String chatName = creatorEmail + " - " + participantEmail;

    // Создаём чат
    Chat chat = chatService.create(creatorEmail, participantEmail, chatName);

    // Создаём участников
    List<ChatMember> chatMembers = new ArrayList<>();
    chatMembers.add(createChatMember(chat, creator, MemberRole.ADMIN));
    chatMembers.add(createChatMember(chat, friend, MemberRole.ADMIN));

    // Созраняем участников в бд
    chatMemberService.saveMembers(chatMembers);
    // Добавляем в чат созданных участников
    chat.getMembers().addAll(chatMembers);

    return get(chat.getId(), creatorEmail);
  }

  private ChatMember createChatMember(Chat chat, User user, MemberRole role) {
    return ChatMember.builder()
        .chat(chat)
        .user(user)
        .role(role)
        .joinDate(ZonedDateTime.now())
        .build();
  }

  private void addMembersToChat(Chat chat, CreateGroupChatDTO request, String creatorEmail) {
    Set<String> allMembersEmails = new HashSet<>(request.membersEmails());
    allMembersEmails.add(creatorEmail);

    List<ChatMember> chatMembers = new ArrayList<>();

    for (String memberEmail : allMembersEmails) {
      User user = userService.getUserByEmail(memberEmail);
      ChatMember member = createChatMember(chat, user,
          creatorEmail.equals(memberEmail) ?
              MemberRole.ADMIN : MemberRole.MEMBER);
      chatMembers.add(member);
    }
    chatMemberService.saveMembers(chatMembers);
    chat.getMembers().addAll(chatMembers);
  }

  @Override
  public void delete(Long chatId, String clientEmail) {
    Chat chat = chatService.get(chatId);

    if (!userService.isAdmin(clientEmail)) {
      try {
        ChatMember member = chatMemberService.getMember(chatId, clientEmail);
        if(!member.getRole().equals(MemberRole.ADMIN)) {
          throw new ChatException("У вас недостаточно прав для удаления этого чата");
        }
      } catch (EntityNotFoundException e) {
        throw new ChatException("Вы не являетесь участником этого чата");
      }
    }

    List<ChatMember> members = chatMemberService.getMembers(chatId);
    members.forEach(chatMemberService::deleteMember);

    chatService.delete(chat);
  }

  @Override
  public ChatDTO get(Long chatId, String clientEmail) {
    if(chatMemberService.isChatMember(chatId, clientEmail)){
      return chatMapper.toChatDTO(chatService.get(chatId));
    }
    throw new ChatException("У вас нет доступа к этому чату, т.к. вы не являетесь участником");
  }
}
