package ru.senla.socialnetwork.services.chats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Nested;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.ZonedDateTime;

import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMemberMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.impl.ChatMemberServiceImpl;
import ru.senla.socialnetwork.services.common.CommonService;

@ExtendWith(MockitoExtension.class)
class ChatMemberServiceImplTest {

  @Mock
  private CommonChatService commonChatService;
  @Mock
  private CommonService commonService;
  @Mock
  private ChatMemberDao chatMemberDao;
  @Mock
  private ChatMemberMapper chatMemberMapper;
  @InjectMocks
  private ChatMemberServiceImpl chatMemberService;

  private User testUser1;
  private User testUser2;
  private Chat testChat;
  private ChatMember testMember1;
  private ChatMember testMember2;
  private ChatMemberDTO testMemberDTO;

  @BeforeEach
  void setUp() {
    testUser1 = new User();
    testUser1.setEmail("user1@test.com");

    testUser2 = new User();
    testUser2.setEmail("user2@test.com");

    testChat = new Chat();
    testChat.setId(1L);
    testChat.setName("Test Chat");
    testChat.setIsGroup(true);

    testMember1 = ChatMember.builder()
        .chat(testChat)
        .user(testUser1)
        .role(MemberRole.ADMIN)
        .joinDate(ZonedDateTime.now())
        .build();
    testMember2 = ChatMember.builder()
        .chat(testChat)
        .user(testUser2)
        .role(MemberRole.MEMBER)
        .joinDate(ZonedDateTime.now())
        .build();

    testMemberDTO = new ChatMemberDTO("user1@test.com", MemberRole.ADMIN,
        ZonedDateTime.now());
  }

  @Nested
  class AddUserToChatTests {
    @Test
    void shouldSuccessfullyAddUserToChat() {
      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(chatMemberDao.countByChatId(1L)).thenReturn(50L);
      when(commonChatService.isChatMember(1L, "user2@test.com")).thenReturn(false);
      when(commonService.getUserByEmail("user2@test.com")).thenReturn(testUser2);
      when(chatMemberDao.saveOrUpdate(any(ChatMember.class))).thenReturn(testMember2);
      when(chatMemberMapper.memberToDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberService.addUserToChat(1L, "user2@test.com");

      assertNotNull(result);
      assertEquals(testMemberDTO, result);
      verify(chatMemberDao).saveOrUpdate(any(ChatMember.class));
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyInChat() {
      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonChatService.isChatMember(1L, "user2@test.com")).thenReturn(true);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.addUserToChat(1L, "user2@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenPersonalChat() {
      testChat.setIsGroup(false);
      when(commonChatService.getChat(1L)).thenReturn(testChat);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.addUserToChat(1L, "user2@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenTooManyMembers() {
      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(chatMemberDao.countByChatId(1L)).thenReturn(100L);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.addUserToChat(1L, "user2@test.com"));
    }
  }

  @Nested
  class RemoveUserFromChatTests {
    @Test
    void shouldSuccessfullyRemoveUserFromChat() {
      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "admin@test.com")).thenReturn(testMember1);

      chatMemberService.removeUserFromChat(1L, "user2@test.com", "admin@test.com");

      verify(chatMemberDao).delete(testMember2);
    }

    @Test
    void shouldThrowExceptionWhenPersonalChat() {
      testChat.setIsGroup(false);
      when(commonChatService.getChat(1L)).thenReturn(testChat);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.removeUserFromChat(1L, "user2@test.com", "user1@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenRemoveSelf() {
      when(commonChatService.getChat(1L)).thenReturn(testChat);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.removeUserFromChat(1L, "user1@test.com", "user1@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenRemoveAdmin() {
      testMember2.setRole(MemberRole.ADMIN);
      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonChatService.getMember(1L, "admin@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.removeUserFromChat(1L, "admin@test.com", "user1@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughPermissions() {
      testMember1.setRole(MemberRole.MEMBER);
      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.removeUserFromChat(1L, "user2@test.com", "user1@test.com"));
    }
  }


  @Nested
  class MuteUserTests {
    @Test
    void shouldSuccessfullyMuteUser() {
      ZonedDateTime muteUntil = ZonedDateTime.now().plusHours(1);

      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "admin@test.com")).thenReturn(testMember1);
      when(chatMemberDao.saveOrUpdate(testMember2)).thenReturn(testMember2);
      when(chatMemberMapper.memberToDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberService.mute(1L, "user2@test.com", muteUntil, "admin@test.com");

      assertNotNull(result);
      assertEquals(muteUntil, testMember2.getMutedUntil());
      assertEquals(testMemberDTO, result);
    }

    @Test
    void shouldThrowExceptionWhenMuteNonMember() {
      testMember2.setRole(MemberRole.ADMIN);
      when(commonChatService.getMember(1L, "admin@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.mute(1L, "admin@test.com", ZonedDateTime.now(), "user1@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughPermissions() {
      testMember1.setRole(MemberRole.MEMBER);
      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.mute(1L, "user2@test.com", ZonedDateTime.now(), "user1@test.com"));
    }
  }

  @Nested
  class LeaveChatTests {
    @Test
    void shouldSuccessfullyLeaveChat() {
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);
      when(chatMemberDao.countByChatIdAndRole(1L, MemberRole.ADMIN)).thenReturn(2L);

      chatMemberService.leave(1L, "user1@test.com");

      verify(chatMemberDao).delete(testMember1);
    }

    @Test
    void shouldThrowExceptionWhenLastAdminLeaves() {
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);
      when(chatMemberDao.countByChatIdAndRole(1L, MemberRole.ADMIN)).thenReturn(1L);

      assertThrows(ChatException.class,
          () -> chatMemberService.leave(1L, "user1@test.com"));
    }
  }

  @Nested
  class ChangeMemberRoleTests {
    @Test
    void shouldSuccessfullyChangeRoleByAdmin() {
      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "admin@test.com")).thenReturn(testMember1);
      when(chatMemberDao.saveOrUpdate(testMember2)).thenReturn(testMember2);
      when(chatMemberMapper.memberToDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberService.changeRole(1L, "user2@test.com", MemberRole.MODERATOR, "admin@test.com");

      assertNotNull(result);
      assertEquals(MemberRole.MODERATOR, testMember2.getRole());
      assertEquals(testMemberDTO, result);
    }

    @Test
    void shouldSuccessfullyChangeRoleByModerator() {
      testMember1.setRole(MemberRole.MODERATOR);
      testMember2.setRole(MemberRole.MEMBER);

      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "moderator@test.com")).thenReturn(testMember1);
      when(chatMemberDao.saveOrUpdate(testMember2)).thenReturn(testMember2);
      when(chatMemberMapper.memberToDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberService.changeRole(1L, "user2@test.com", MemberRole.MODERATOR, "moderator@test.com");

      assertNotNull(result);
      assertEquals(MemberRole.MODERATOR, testMember2.getRole());
    }

    @Test
    void shouldThrowExceptionWhenChangeAdminRole() {
      testMember2.setRole(MemberRole.ADMIN);
      testMember1.setRole(MemberRole.MODERATOR);

      when(commonChatService.getMember(1L, "admin@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "moderator@test.com")).thenReturn(testMember1);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.changeRole(1L, "admin@test.com", MemberRole.MEMBER, "moderator@test.com"));
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughPermissions() {
      testMember1.setRole(MemberRole.MEMBER);

      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);

      assertThrows(ChatMemberException.class,
          () -> chatMemberService.changeRole(1L, "user2@test.com", MemberRole.MODERATOR, "user1@test.com"));
    }
  }
}