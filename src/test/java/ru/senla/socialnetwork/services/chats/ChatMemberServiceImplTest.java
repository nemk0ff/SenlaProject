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

      assertThrows(ChatException.class,
          () -> chatMemberService.addUserToChat(1L, "user2@test.com"));
    }
  }

  @Nested
  class RemoveUserFromChatTests {
    @Test
    void shouldSuccessfullyRemoveUserFromChat() {
      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);

      chatMemberService.removeUserFromChat(1L, "user2@test.com");

      verify(chatMemberDao).delete(testMember2);
    }
  }

  @Nested
  class MuteUserTests {
    @Test
    void shouldSuccessfullyMuteUser() {
      ZonedDateTime muteUntil = ZonedDateTime.now().plusHours(1);

      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(chatMemberDao.saveOrUpdate(testMember2)).thenReturn(testMember2);
      when(chatMemberMapper.memberToDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberService.muteUser(1L, "user2@test.com", muteUntil);

      assertNotNull(result);
      assertEquals(muteUntil, testMember2.getMutedUntil());
      assertEquals(testMemberDTO, result);
    }
  }

  @Nested
  class LeaveChatTests {
    @Test
    void shouldSuccessfullyLeaveChat() {
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);
      when(chatMemberDao.countByChatIdAndRole(1L, MemberRole.ADMIN)).thenReturn(2L);

      chatMemberService.leaveChat(1L, "user1@test.com");

      verify(chatMemberDao).delete(testMember1);
    }

    @Test
    void shouldThrowExceptionWhenLastAdminLeaves() {
      when(commonChatService.getMember(1L, "user1@test.com")).thenReturn(testMember1);
      when(chatMemberDao.countByChatIdAndRole(1L, MemberRole.ADMIN)).thenReturn(1L);

      assertThrows(ChatException.class,
          () -> chatMemberService.leaveChat(1L, "user1@test.com"));
    }
  }

  @Nested
  class ChangeMemberRoleTests {
    @Test
    void shouldSuccessfullyChangeRole() {
      when(commonChatService.getMember(1L, "user2@test.com")).thenReturn(testMember2);
      when(chatMemberDao.saveOrUpdate(testMember2)).thenReturn(testMember2);
      when(chatMemberMapper.memberToDTO(testMember2)).thenReturn(testMemberDTO);

      ChatMemberDTO result = chatMemberService.changeMemberRole(1L, "user2@test.com", MemberRole.ADMIN);

      assertNotNull(result);
      assertEquals(MemberRole.ADMIN, testMember2.getRole());
      assertEquals(testMemberDTO, result);
    }
  }
}