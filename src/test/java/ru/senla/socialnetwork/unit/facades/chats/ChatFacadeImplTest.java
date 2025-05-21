package ru.senla.socialnetwork.unit.facades.chats;

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
import static ru.senla.socialnetwork.unit.TestConstants.*;

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
        .id(TEST_USER_ID_1)
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
      when(chatService.getAllByUser(TEST_USER_ID_1)).thenReturn(chats);
      when(chatMapper.toListChatDTO(chats)).thenReturn(expectedDTOs);

      List<ChatDTO> result = chatFacade.getUserChats(TEST_EMAIL_1);

      assertThat(result).isEqualTo(expectedDTOs);
      verify(userService).getUserByEmail(TEST_EMAIL_1);
      verify(chatService).getAllByUser(TEST_USER_ID_1);
      verify(chatMapper).toListChatDTO(chats);
    }
  }

  @Nested
  class CreateGroupChatTests {

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