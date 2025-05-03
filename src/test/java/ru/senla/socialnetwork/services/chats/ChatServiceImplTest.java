package ru.senla.socialnetwork.services.chats;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Nested;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
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
import ru.senla.socialnetwork.services.chats.impl.ChatServiceImpl;
import ru.senla.socialnetwork.services.common.CommonService;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

  @Mock
  private CommonService commonService;
  @Mock
  private CommonChatService commonChatService;
  @Mock
  private ChatMapper chatMapper;
  @Mock
  private ChatDao chatDao;
  @Mock
  private ChatMemberDao chatMemberDao;
  @InjectMocks
  private ChatServiceImpl chatService;

  private User testUser1;
  private User testUser2;
  private Chat testChat;
  private ChatDTO testChatDTO;
  private ChatMember testMember1;
  private ChatMember testMember2;

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
    testChat.setCreatedAt(ZonedDateTime.now());

    testChatDTO = new ChatDTO(1L, "Test Chat", true,
        ZonedDateTime.now(), new ArrayList<>());

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
  }

  @Nested
  class createGroupChatTests {
    @Test
    void ShouldSuccessfullyCreateChat() {
      CreateGroupChatDTO request = new CreateGroupChatDTO(
          "user1@test.com",
          "Group Chat",
          Set.of("user2@test.com")
      );

      when(commonService.existsByEmail("user1@test.com")).thenReturn(true);
      when(commonService.getUserByEmail("user1@test.com")).thenReturn(testUser1);
      when(commonService.getUserByEmail("user2@test.com")).thenReturn(testUser2);
      when(chatDao.saveOrUpdate(any())).thenReturn(testChat);
      when(chatMapper.chatToChatDTO(any())).thenReturn(testChatDTO);

      ChatDTO result = chatService.create(request);

      assertNotNull(result);
      assertEquals(testChatDTO, result);
      verify(chatMemberDao).saveAll(anyList());
      verify(chatDao).saveOrUpdate(any(Chat.class));
    }

    @Test
    void ShouldThrowExceptionWhenCreatorNotRegistered() {
      CreateGroupChatDTO request = new CreateGroupChatDTO(
          "user1@test.com",
          "Group Chat",
          Set.of("user2@test.com")
      );

      when(commonService.existsByEmail("user1@test.com")).thenReturn(false);

      assertThrows(UserNotRegisteredException.class, () -> chatService.create(request));
    }

    @Test
    void createGroupChat_ShouldThrowExceptionWhenNoMembers() {
      CreateGroupChatDTO request = new CreateGroupChatDTO(
          "not_existed_user@test.com",
          "Group Chat",
          Collections.emptySet()
      );

      when(commonService.existsByEmail("not_existed_user@test.com")).thenReturn(true);

      assertThrows(ChatException.class, () -> chatService.create(request));
    }
  }


  @Nested
  class createPersonalChatTest {
    @Test
    void ShouldSuccessfullyCreateChat() {
      CreatePersonalChatDTO request = new CreatePersonalChatDTO(
          "user1@test.com",
          "user2@test.com"
      );

      when(commonService.getUserByEmail("user1@test.com")).thenReturn(testUser1);
      when(commonService.getUserByEmail("user2@test.com")).thenReturn(testUser2);
      when(chatDao.existsByMembers("user1@test.com", "user2@test.com")).thenReturn(false);
      when(chatDao.saveOrUpdate(any())).thenReturn(testChat);
      when(chatMapper.chatToChatDTO(any())).thenReturn(testChatDTO);

      ChatDTO result = chatService.create(request);

      assertNotNull(result);
      assertEquals(testChatDTO, result);
      verify(chatMemberDao).saveAll(anyList());
      verify(chatDao).saveOrUpdate(any(Chat.class));
    }

    @Test
    void ShouldThrowExceptionWhenChatExists() {
      CreatePersonalChatDTO request = new CreatePersonalChatDTO(
          "user1@test.com",
          "user2@test.com"
      );

      when(commonService.getUserByEmail("user1@test.com")).thenReturn(testUser1);
      when(commonService.getUserByEmail("user2@test.com")).thenReturn(testUser2);
      when(chatDao.existsByMembers("user1@test.com", "user2@test.com")).thenReturn(true);

      assertThrows(ChatException.class, () -> chatService.create(request));
    }
  }


  @Test
  void deleteChat_ShouldSuccessfullyDeleteChat() {
    when(commonChatService.getChat(1L)).thenReturn(testChat);
    when(chatMemberDao.findMembersByChatId(1L)).thenReturn(List.of(testMember1, testMember2));

    chatService.delete(1L);

    verify(chatMemberDao).delete(testMember1);
    verify(chatMemberDao).delete(testMember2);
    verify(chatDao).delete(testChat);
  }

  @Test
  void getChat_ShouldReturnChatDTO() {
    when(commonChatService.getChat(1L)).thenReturn(testChat);
    when(chatMapper.chatToChatDTO(testChat)).thenReturn(testChatDTO);

    ChatDTO result = chatService.get(1L);

    assertNotNull(result);
    assertEquals(testChatDTO, result);
  }
}
