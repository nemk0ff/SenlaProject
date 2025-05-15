package ru.senla.socialnetwork.facades;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.TestConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
import ru.senla.socialnetwork.facades.chats.impl.ChatFacadeImpl;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.ChatService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class ChatFacadeImplTest {
  @Mock
  private ChatMapper chatMapper;
  @Mock
  private ChatService chatService;
  @Mock
  private ChatMemberService chatMemberService;
  @Mock
  private UserService userService;

  @InjectMocks
  private ChatFacadeImpl chatFacade;

  private Chat testChat;
  private ChatDTO testChatDTO;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID)
        .email(TEST_EMAIL_1)
        .build();

    testChat = Chat.builder()
        .id(TEST_CHAT_ID)
        .name(TEST_CHAT_NAME)
        .createdAt(TEST_DATE)
        .isGroup(true)
        .members(new ArrayList<>())
        .build();

    testChatDTO = new ChatDTO(
        TEST_CHAT_ID,
        TEST_CHAT_NAME,
        true,
        TEST_DATE,
        null);
  }

  @Nested
  class GetUserChatsTests {
    @Test
    void getUserChats_whenUserExists_thenReturnChatDTOs() {
      List<Chat> chats = List.of(testChat);
      List<ChatDTO> expectedDTOs = List.of(testChatDTO);

      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(chatService.getAllByUser(TEST_USER_ID)).thenReturn(chats);
      when(chatMapper.toListChatDTO(chats)).thenReturn(expectedDTOs);

      List<ChatDTO> result = chatFacade.getUserChats(TEST_EMAIL_1);

      assertThat(result).isEqualTo(expectedDTOs);
      verify(userService).getUserByEmail(TEST_EMAIL_1);
      verify(chatService).getAllByUser(TEST_USER_ID);
      verify(chatMapper).toListChatDTO(chats);
    }

    @Test
    void getUserChats_whenNoChats_thenReturnEmptyList() {
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(testUser);
      when(chatService.getAllByUser(TEST_USER_ID)).thenReturn(List.of());
      when(chatMapper.toListChatDTO(List.of())).thenReturn(List.of());

      List<ChatDTO> result = chatFacade.getUserChats(TEST_EMAIL_1);

      assertThat(result).isEmpty();
      verify(userService).getUserByEmail(TEST_EMAIL_1);
      verify(chatService).getAllByUser(TEST_USER_ID);
      verify(chatMapper).toListChatDTO(List.of());
    }
  }

  @Nested
  class CreateGroupChatTests {
    @Test
    void create_whenValidGroupChat_thenReturnChatDTO() {
      User creatorUser = User.builder().id(1L).email(TEST_EMAIL_1).build();
      User memberUser = User.builder().id(2L).email(TEST_EMAIL_2).build();
      Chat createdChat = Chat.builder()
          .id(TEST_CHAT_ID)
          .name(TEST_CHAT_NAME)
          .isGroup(true)
          .createdAt(ZonedDateTime.now())
          .members(new ArrayList<>())
          .build();
      CreateGroupChatDTO request = new CreateGroupChatDTO(
          TEST_CHAT_NAME,
          Set.of(TEST_EMAIL_2));
      ChatDTO expectedDto = new ChatDTO(
          TEST_CHAT_ID,
          TEST_CHAT_NAME,
          true,
          createdChat.getCreatedAt(),
          null);

      when(chatService.create(request)).thenReturn(createdChat);
      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(creatorUser);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(memberUser);

      doAnswer(invocation -> {
        List<ChatMember> members = invocation.getArgument(0);
        createdChat.getMembers().addAll(members);
        return null;
      }).when(chatMemberService).saveMembers(anyList());

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(createdChat);
      when(chatMapper.toChatDTO(createdChat)).thenReturn(expectedDto);

      ChatDTO result = chatFacade.create(request, TEST_EMAIL_1);

      assertThat(result).isEqualTo(expectedDto);
      verify(chatMemberService).saveMembers(argThat(members ->
          members.size() == 2 &&
              members.stream().anyMatch(m ->
                  m.getUser().getEmail().equals(TEST_EMAIL_1) &&
                      m.getRole() == MemberRole.ADMIN
              ) &&
              members.stream().anyMatch(m ->
                  m.getUser().getEmail().equals(TEST_EMAIL_2) &&
                      m.getRole() == MemberRole.MEMBER
              )
      ));
      verify(chatMemberService).isChatMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(chatService).get(TEST_CHAT_ID);
    }
  }

  @Nested
  class CreatePersonalChatTests {
    @Test
    void create_whenValidPersonalChat_thenReturnChatDTO() {
      User creator = User.builder().id(1L).email(TEST_EMAIL_1).build();
      User participant = User.builder().id(2L).email(TEST_EMAIL_2).build();

      String chatName = TEST_EMAIL_1 + " - " + TEST_EMAIL_2;
      Chat createdChat = Chat.builder()
          .id(TEST_CHAT_ID)
          .name(chatName)
          .isGroup(false)
          .createdAt(ZonedDateTime.now())
          .members(new ArrayList<>())
          .build();

      ChatDTO expectedDto = new ChatDTO(
          TEST_CHAT_ID,
          chatName,
          false,
          createdChat.getCreatedAt(),
          null);

      when(chatService.create(TEST_EMAIL_1, TEST_EMAIL_2, chatName))
          .thenReturn(createdChat);

      when(userService.getUserByEmail(TEST_EMAIL_1)).thenReturn(creator);
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(participant);

      doAnswer(invocation -> {
        List<ChatMember> members = invocation.getArgument(0);
        createdChat.getMembers().addAll(members);
        return null;
      }).when(chatMemberService).saveMembers(any());

      when(chatMapper.toChatDTO(createdChat)).thenReturn(expectedDto);

      ChatDTO result = chatFacade.create(TEST_EMAIL_1, TEST_EMAIL_2);

      assertThat(result)
          .isNotNull()
          .isEqualTo(expectedDto);

      verify(chatService).create(TEST_EMAIL_1, TEST_EMAIL_2, chatName);
      verify(userService).getUserByEmail(TEST_EMAIL_1);
      verify(userService).getUserByEmail(TEST_EMAIL_2);
      verify(chatMemberService).saveMembers(any());
      verify(chatMapper).toChatDTO(createdChat);

      assertThat(createdChat.getMembers())
          .hasSize(2)
          .extracting("user.email")
          .containsExactlyInAnyOrder(TEST_EMAIL_1, TEST_EMAIL_2);
    }

    @Test
    void create_whenChatExists_thenThrowException() {
      when(chatService.create(TEST_EMAIL_1, TEST_EMAIL_2, TEST_EMAIL_1 + " - " + TEST_EMAIL_2))
          .thenThrow(new ChatException("Личный чат " + TEST_EMAIL_1 + " - " + TEST_EMAIL_2 + " уже существует"));

      assertThatThrownBy(() -> chatFacade.create(TEST_EMAIL_1, TEST_EMAIL_2))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("Личный чат " + TEST_EMAIL_1 + " - " + TEST_EMAIL_2 + " уже существует");
    }
  }

  @Nested
  class DeleteChatTests {
    @Test
    void delete_whenAdmin_thenDeleteChat() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(true);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMembers(TEST_CHAT_ID)).thenReturn(List.of());

      chatFacade.delete(TEST_CHAT_ID, TEST_EMAIL_1);

      verify(userService).isAdmin(TEST_EMAIL_1);
      verify(chatService).get(TEST_CHAT_ID);
      verify(chatMemberService).getMembers(TEST_CHAT_ID);
      verify(chatService).delete(testChat);
    }

    @Test
    void delete_whenChatAdmin_thenDeleteChat() {
      ChatMember adminMember = ChatMember.builder()
          .user(testUser)
          .role(MemberRole.ADMIN)
          .build();

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(adminMember);
      when(chatMemberService.getMembers(TEST_CHAT_ID)).thenReturn(List.of());

      chatFacade.delete(TEST_CHAT_ID, TEST_EMAIL_1);

      verify(userService).isAdmin(TEST_EMAIL_1);
      verify(chatService).get(TEST_CHAT_ID);
      verify(chatMemberService).getMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(chatMemberService).getMembers(TEST_CHAT_ID);
      verify(chatService).delete(testChat);
    }

    @Test
    void delete_whenNotAdminNorChatAdmin_thenThrowException() {
      ChatMember regularMember = ChatMember.builder()
          .user(testUser)
          .role(MemberRole.MEMBER)
          .build();

      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(regularMember);

      assertThatThrownBy(() -> chatFacade.delete(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("У вас недостаточно прав для удаления этого чата");
    }

    @Test
    void delete_whenNotChatMember_thenThrowException() {
      when(userService.isAdmin(TEST_EMAIL_1)).thenReturn(false);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenThrow(new EntityNotFoundException("Участник чата не найден"));

      assertThatThrownBy(() -> chatFacade.delete(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("Вы не являетесь участником этого чата");
    }
  }

  @Nested
  class GetChatTests {
    @Test
    void get_whenChatMember_thenReturnChatDTO() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMapper.toChatDTO(testChat)).thenReturn(testChatDTO);

      ChatDTO result = chatFacade.get(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testChatDTO);
      verify(chatMemberService).isChatMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(chatService).get(TEST_CHAT_ID);
      verify(chatMapper).toChatDTO(testChat);
    }

    @Test
    void get_whenNotChatMember_thenThrowException() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(false);

      assertThatThrownBy(() -> chatFacade.get(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("У вас нет доступа к этому чату, т.к. вы не являетесь участником");
    }
  }
}