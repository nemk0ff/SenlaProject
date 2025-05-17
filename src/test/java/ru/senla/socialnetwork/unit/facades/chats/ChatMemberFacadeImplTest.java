package ru.senla.socialnetwork.unit.facades.chats;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.unit.TestConstants.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMemberMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.facades.chats.impl.ChatMemberFacadeImpl;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.ChatService;
import ru.senla.socialnetwork.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class ChatMemberFacadeImplTest {
  @Mock
  private ChatMemberMapper chatMemberMapper;
  @Mock
  private ChatService chatService;
  @Mock
  private ChatMemberService chatMemberService;
  @Mock
  private UserService userService;

  @InjectMocks
  private ChatMemberFacadeImpl chatMemberFacade;

  private Chat testChat;
  private User testUser1;
  private User testUser2;
  private ChatMember testMember1;
  private ChatMember testMember2;
  private ChatMemberDTO testMemberDTO;

  @BeforeEach
  void setUp() {
    testUser1 = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .build();

    testUser2 = User.builder()
        .id(TEST_USER_ID_2)
        .email(TEST_EMAIL_2)
        .build();

    testChat = Chat.builder()
        .id(TEST_CHAT_ID)
        .name(TEST_CHAT_NAME)
        .isGroup(true)
        .createdAt(TEST_DATE)
        .members(new ArrayList<>())
        .build();

    testMember1 = ChatMember.builder()
        .id(1L)
        .user(testUser1)
        .chat(testChat)
        .role(MemberRole.ADMIN)
        .joinDate(TEST_DATE)
        .build();

    testMember2 = ChatMember.builder()
        .id(2L)
        .user(testUser2)
        .chat(testChat)
        .role(MemberRole.MEMBER)
        .joinDate(TEST_DATE)
        .build();

    testMemberDTO = new ChatMemberDTO(
        TEST_EMAIL_1,
        TEST_CHAT_ID,
        TEST_CHAT_NAME,
        MemberRole.ADMIN,
        TEST_DATE,
        null);
  }

  @Nested
  class AddUserToChatTests {
    @Test
    void addUserToChat_whenNotChatMember_thenThrowException() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(false);

      assertThatThrownBy(() -> chatMemberFacade.addUserToChat(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Вы не можете добавить участника, т.к. не являетесь участником этого чата");
    }

    @Test
    void addUserToChat_whenUserAlreadyInChat_thenThrowException() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMaybeMember(TEST_CHAT_ID, TEST_EMAIL_2))
          .thenReturn(Optional.of(testMember2));

      assertThatThrownBy(() -> chatMemberFacade.addUserToChat(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Пользователь уже в чате");
    }

    @Test
    void addUserToChat_whenUserWasInChatBefore_thenRecreateMember() {
      ChatMember leftMember = ChatMember.builder()
          .id(2L)
          .user(testUser2)
          .chat(testChat)
          .role(MemberRole.MEMBER)
          .joinDate(TEST_DATE)
          .leaveDate(TEST_DATE)
          .build();

      ChatMember recreatedMember = ChatMember.builder()
          .id(3L)
          .user(testUser2)
          .chat(testChat)
          .role(MemberRole.MEMBER)
          .joinDate(TEST_DATE.plusDays(1))
          .build();

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMaybeMember(TEST_CHAT_ID, TEST_EMAIL_2))
          .thenReturn(Optional.of(leftMember));
      when(chatMemberService.recreate(leftMember)).thenReturn(recreatedMember);
      when(chatMemberMapper.toDTO(recreatedMember)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberFacade.addUserToChat(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMemberDTO);
      verify(chatMemberService).recreate(leftMember);
    }

    @Test
    void addUserToChat_whenNewUser_thenAddToChat() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMaybeMember(TEST_CHAT_ID, TEST_EMAIL_2))
          .thenReturn(Optional.empty());
      when(userService.getUserByEmail(TEST_EMAIL_2)).thenReturn(testUser2);
      when(chatMemberService.addUserToChat(testChat, testUser2)).thenReturn(testMember2);
      when(chatMemberMapper.toDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberFacade.addUserToChat(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMemberDTO);
      verify(userService).getUserByEmail(TEST_EMAIL_2);
      verify(chatMemberService).addUserToChat(testChat, testUser2);
    }
  }

  @Nested
  class RemoveUserTests {
    @Test
    void removeUser_whenNotGroupChat_thenThrowException() {
      testChat.setIsGroup(false);
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);

      assertThatThrownBy(() -> chatMemberFacade.removeUser(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Нельзя удалить участника из личного чата");
    }

    @Test
    void removeUser_whenRemovingSelf_thenThrowException() {
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);

      assertThatThrownBy(() -> chatMemberFacade.removeUser(TEST_CHAT_ID, TEST_EMAIL_1, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Нельзя удалить самого себя");
    }

    @Test
    void removeUser_whenMemberTriesToRemove_thenThrowException() {
      ChatMember member = ChatMember.builder()
          .user(testUser1)
          .role(MemberRole.MEMBER)
          .build();

      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(member);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_2)).thenReturn(testMember2);

      assertThatThrownBy(() -> chatMemberFacade.removeUser(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("У вас недостаточно прав для удаления пользователя");
    }

    @Test
    void removeUser_whenModeratorTriesToRemoveNonMember_thenThrowException() {
      ChatMember moderator = ChatMember.builder()
          .user(testUser1)
          .role(MemberRole.MODERATOR)
          .build();

      ChatMember adminToRemove = ChatMember.builder()
          .user(testUser2)
          .role(MemberRole.ADMIN)
          .build();

      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(moderator);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_2)).thenReturn(adminToRemove);

      assertThatThrownBy(() -> chatMemberFacade.removeUser(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Вы можете удалить из чата только обычного участника");
    }

    @Test
    void removeUser_whenValidRequest_thenRemoveMember() {
      when(chatService.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember1);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_2)).thenReturn(testMember2);
      when(chatMemberService.removeMember(testMember2)).thenReturn(testMember2);
      when(chatMemberMapper.toDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberFacade.removeUser(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMemberDTO);
      verify(chatMemberService).removeMember(testMember2);
    }
  }

  @Nested
  class MuteTests {
    @Test
    void mute_whenMemberTriesToMute_thenThrowException() {
      ChatMember member = ChatMember.builder()
          .user(testUser1)
          .role(MemberRole.MEMBER)
          .build();

      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(member);

      assertThatThrownBy(() -> chatMemberFacade.mute(TEST_CHAT_ID, TEST_EMAIL_2, TEST_FUTURE_DATE, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("У вас недостаточно прав, чтобы выдавать мут");
    }

    @Test
    void mute_whenValidRequest_thenMuteMember() {
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember1);
      when(chatMemberService.mute(TEST_CHAT_ID, TEST_EMAIL_2, TEST_FUTURE_DATE)).thenReturn(testMember2);
      when(chatMemberMapper.toDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberFacade.mute(TEST_CHAT_ID, TEST_EMAIL_2, TEST_FUTURE_DATE, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMemberDTO);
      verify(chatMemberService).mute(TEST_CHAT_ID, TEST_EMAIL_2, TEST_FUTURE_DATE);
    }
  }

  @Nested
  class UnmuteTests {
    @Test
    void unmute_whenMemberTriesToUnmute_thenThrowException() {
      ChatMember member = ChatMember.builder()
          .user(testUser1)
          .role(MemberRole.MEMBER)
          .build();

      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(member);

      assertThatThrownBy(() -> chatMemberFacade.unmute(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("У вас недостаточно прав, чтобы снимать мут");
    }

    @Test
    void unmute_whenValidRequest_thenUnmuteMember() {
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember1);
      when(chatMemberService.unmute(TEST_CHAT_ID, TEST_EMAIL_2)).thenReturn(testMember2);
      when(chatMemberMapper.toDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberFacade.unmute(TEST_CHAT_ID, TEST_EMAIL_2, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMemberDTO);
      verify(chatMemberService).unmute(TEST_CHAT_ID, TEST_EMAIL_2);
    }
  }

  @Nested
  class LeaveTests {
    @Test
    void leave_whenValidRequest_thenLeaveChat() {
      when(chatMemberService.leave(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember1);
      when(chatMemberMapper.toDTO(testMember1)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberFacade.leave(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMemberDTO);
      verify(chatMemberService).leave(TEST_CHAT_ID, TEST_EMAIL_1);
    }
  }

  @Nested
  class ChangeRoleTests {
    @Test
    void changeRole_whenChangingOwnRole_thenThrowException() {
      assertThatThrownBy(() -> chatMemberFacade.changeRole(TEST_CHAT_ID, TEST_EMAIL_1, MemberRole.MODERATOR, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Вы не можете изменить свою роль в чате");
    }

    @Test
    void changeRole_whenNotAdmin_thenThrowException() {
      ChatMember moderator = ChatMember.builder()
          .user(testUser1)
          .role(MemberRole.MODERATOR)
          .build();

      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(moderator);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_2)).thenReturn(testMember2);

      assertThatThrownBy(() -> chatMemberFacade.changeRole(TEST_CHAT_ID, TEST_EMAIL_2, MemberRole.MODERATOR, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("У вас нет прав, чтобы изменить роль этого участника чата");
    }

    @Test
    void changeRole_whenTryingToChangeAdmin_thenThrowException() {
      ChatMember adminToChange = ChatMember.builder()
          .user(testUser2)
          .role(MemberRole.ADMIN)
          .build();

      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember1);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_2)).thenReturn(adminToChange);

      assertThatThrownBy(() -> chatMemberFacade.changeRole(TEST_CHAT_ID, TEST_EMAIL_2, MemberRole.MODERATOR, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("У вас нет прав, чтобы изменить роль этого участника чата");
    }

    @Test
    void changeRole_whenValidRequest_thenChangeRole() {
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember1);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_2)).thenReturn(testMember2);
      when(chatMemberService.changeRole(TEST_CHAT_ID, testMember2, MemberRole.MODERATOR)).thenReturn(testMember2);
      when(chatMemberMapper.toDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberFacade.changeRole(TEST_CHAT_ID, TEST_EMAIL_2, MemberRole.MODERATOR, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMemberDTO);
      verify(chatMemberService).changeRole(TEST_CHAT_ID, testMember2, MemberRole.MODERATOR);
    }
  }
}