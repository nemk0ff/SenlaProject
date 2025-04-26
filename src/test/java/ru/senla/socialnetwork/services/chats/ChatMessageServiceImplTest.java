package ru.senla.socialnetwork.services.chats;

import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import ru.senla.socialnetwork.dao.chats.ChatMessageDao;
import ru.senla.socialnetwork.dto.chats.ChatMessageDTO;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMessageMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMessageException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.ChatMessage;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.impl.ChatMessageServiceImpl;
import ru.senla.socialnetwork.services.common.CommonService;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

  @Mock
  private CommonChatService commonChatService;
  @Mock
  private CommonService commonService;
  @Mock
  private ChatMessageDao chatMessageDao;
  @Mock
  private ChatMessageMapper chatMessageMapper;
  @InjectMocks
  private ChatMessageServiceImpl chatMessageService;

  private User testUser;
  private Chat testChat;
  private ChatMember testMember;
  private ChatMessage testMessage;
  private ChatMessage repliedMessage;
  private final ZonedDateTime now = ZonedDateTime.now();

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setEmail("user@test.com");

    testChat = new Chat();
    testChat.setId(1L);
    testChat.setName("Test Chat");

    testMember = ChatMember.builder()
        .chat(testChat)
        .user(testUser)
        .role(MemberRole.MEMBER)
        .joinDate(now)
        .mutedUntil(null)
        .build();

    repliedMessage = ChatMessage.builder()
        .id(2L)
        .chat(testChat)
        .author(testUser)
        .body("Replied message")
        .createdAt(now.minusHours(1))
        .isPinned(false)
        .build();
    testMessage = ChatMessage.builder()
        .id(1L)
        .chat(testChat)
        .author(testUser)
        .body("Test message")
        .createdAt(now)
        .isPinned(false)
        .replyTo(repliedMessage)
        .build();
  }

  @Nested
  class SendMessageTests {
    @Test
    void shouldSuccessfullySendMessage() {
      CreateMessageDTO request = new CreateMessageDTO(
          "user@test.com", "Test message", null);

      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonService.getUserByEmail("user@test.com")).thenReturn(testUser);
      when(commonChatService.getMember(1L, "user@test.com")).thenReturn(testMember);
      when(chatMessageDao.saveOrUpdate(any(ChatMessage.class))).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(new ChatMessageDTO(
          1L, "Test message", "user@test.com", now, null, false));

      ChatMessageDTO result = chatMessageService.sendMessage(
          1L, "user@test.com", request);

      assertNotNull(result);
      assertEquals("Test message", result.body());
      assertEquals("user@test.com", result.authorEmail());
      verify(chatMessageDao).saveOrUpdate(any(ChatMessage.class));
    }

    @Test
    void shouldSuccessfullySendReplyMessage() {
      CreateMessageDTO request = new CreateMessageDTO(
          "user@test.com", "Test reply", 2L);

      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonService.getUserByEmail("user@test.com")).thenReturn(testUser);
      when(commonChatService.getMember(1L, "user@test.com")).thenReturn(testMember);
      when(chatMessageDao.find(2L)).thenReturn(Optional.of(repliedMessage));
      when(chatMessageDao.saveOrUpdate(any(ChatMessage.class))).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(new ChatMessageDTO
          (1L, "Test reply", "user@test.com", now, 2L, false));

      ChatMessageDTO result = chatMessageService.sendMessage(
          1L, "user@test.com", request);

      assertNotNull(result);
      assertEquals(2L, result.replyToId());
      assertNotNull(testMessage.getReplyTo());
      assertEquals(2L, testMessage.getReplyTo().getId());
    }

    @Test
    void shouldThrowExceptionWhenMuted() {
      testMember.setMutedUntil(now.plusHours(1));
      CreateMessageDTO request = new CreateMessageDTO(
          "user@test.com", "Test message", null);

      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonService.getUserByEmail("user@test.com")).thenReturn(testUser);
      when(commonChatService.getMember(1L, "user@test.com")).thenReturn(testMember);

      ChatMessageException exception = assertThrows(ChatMessageException.class,
          () -> chatMessageService.sendMessage(1L, "user@test.com", request));

      assertEquals("Вы замьючены до " + now.plusHours(1), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReplyMessageNotFound() {
      CreateMessageDTO request = new CreateMessageDTO(
          "user@test.com", "Test reply", 999L);

      when(commonChatService.getChat(1L)).thenReturn(testChat);
      when(commonService.getUserByEmail("user@test.com")).thenReturn(testUser);
      when(commonChatService.getMember(1L, "user@test.com")).thenReturn(testMember);
      when(chatMessageDao.find(999L)).thenReturn(Optional.empty());

      assertThrows(ChatMessageException.class,
          () -> chatMessageService.sendMessage(1L, "user@test.com", request));
    }
  }

  @Nested
  class GetMessagesTests {
    @Test
    void shouldReturnMessagesList() {
      List<ChatMessage> messages = List.of(testMessage, repliedMessage);

      when(chatMessageDao.findByChatId(1L)).thenReturn(messages);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(new ChatMessageDTO(
          1L, "Test message", "user@test.com", now, null, false));
      when(chatMessageMapper.toDTO(repliedMessage)).thenReturn(new ChatMessageDTO(
          2L, "Replied message", "user@test.com", now.minusHours(1), null, false));

      List<ChatMessageDTO> result = chatMessageService.getMessages(1L);

      assertNotNull(result);
      assertEquals(2, result.size());
      assertEquals("Test message", result.get(0).body());
      assertEquals("Replied message", result.get(1).body());
    }
  }

  @Nested
  class PinMessageTests {
    @Test
    void shouldSuccessfullyPinMessage() {
      testMessage.setIsPinned(false);
      ChatMessageDTO pinnedMessageDTO = new ChatMessageDTO(
          1L, "Test message", "user@test.com", now, null, true);

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));
      when(chatMessageDao.saveOrUpdate(testMessage)).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(pinnedMessageDTO);

      ChatMessageDTO result = chatMessageService.pinMessage(1L, 1L);

      assertTrue(result.isPinned());
      assertTrue(testMessage.getIsPinned());
    }

    @Test
    void shouldThrowExceptionWhenMessageAlreadyPinned() {
      testMessage.setIsPinned(true);

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));

      ChatMessageException exception = assertThrows(ChatMessageException.class,
          () -> chatMessageService.pinMessage(1L, 1L));
      assertEquals("Это сообщение уже закреплено.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMessageNotInChat() {
      Chat otherChat = new Chat();
      otherChat.setId(2L);
      testMessage.setChat(otherChat);

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));

      ChatMessageException exception = assertThrows(ChatMessageException.class,
          () -> chatMessageService.pinMessage(1L, 1L));

      assertEquals("Сообщение не принадлежит этому чату", exception.getMessage());
    }
  }

  @Nested
  class UnpinMessageTests {
    @Test
    void shouldSuccessfullyUnpinMessage() {
      testMessage.setIsPinned(true);
      ChatMessageDTO unpinnedMessageDTO = new ChatMessageDTO(
          1L, "Test message", "user@test.com", now, null, false);

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));
      when(chatMessageDao.saveOrUpdate(testMessage)).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(unpinnedMessageDTO);

      ChatMessageDTO result = chatMessageService.unpinMessage(1L, 1L);

      assertFalse(result.isPinned());
      assertFalse(testMessage.getIsPinned());
    }

    @Test
    void shouldThrowExceptionWhenMessageNotPinned() {
      testMessage.setIsPinned(false);

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));

      ChatMessageException exception = assertThrows(ChatMessageException.class,
          () -> chatMessageService.unpinMessage(1L, 1L));

      assertEquals("Это сообщение не закреплено.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMessageNotFound() {
      when(chatMessageDao.find(1L)).thenReturn(Optional.empty());

      assertThrows(ChatMessageException.class,
          () -> chatMessageService.unpinMessage(1L, 1L));
    }
  }

  @Nested
  class DeleteMessageTests {
    @Test
    void shouldSuccessfullyDeleteOwnMessage() {
      String currentUserEmail = "user@test.com";
      testMessage.setAuthor(testUser);

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));
      when(commonChatService.getMember(1L, currentUserEmail)).thenReturn(testMember);

      chatMessageService.deleteMessage(1L, 1L, currentUserEmail);

      verify(chatMessageDao).delete(testMessage);
    }

    @Test
    void shouldSuccessfullyDeleteMessageByAdmin() {
      String adminEmail = "admin@test.com";
      User admin = new User();
      admin.setEmail(adminEmail);
      ChatMember adminMember = ChatMember.builder()
          .chat(testChat)
          .user(admin)
          .role(MemberRole.ADMIN)
          .build();

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));
      when(commonChatService.getMember(1L, adminEmail)).thenReturn(adminMember);

      chatMessageService.deleteMessage(1L, 1L, adminEmail);

      verify(chatMessageDao).delete(testMessage);
    }

    @Test
    void shouldSuccessfullyDeleteMessageByModerator() {
      String adminEmail = "admin@test.com";
      User admin = new User();
      admin.setEmail(adminEmail);
      ChatMember adminMember = ChatMember.builder()
          .chat(testChat)
          .user(admin)
          .role(MemberRole.MODERATOR)
          .build();

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));
      when(commonChatService.getMember(1L, adminEmail)).thenReturn(adminMember);

      chatMessageService.deleteMessage(1L, 1L, adminEmail);

      verify(chatMessageDao).delete(testMessage);
    }

    @Test
    void shouldThrowExceptionWhenHaveNotPermission() {
      String otherUserEmail = "other@test.com";
      User otherUser = new User();
      otherUser.setEmail(otherUserEmail);
      ChatMember otherMember = ChatMember.builder()
          .chat(testChat)
          .user(otherUser)
          .role(MemberRole.MEMBER)
          .build();

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));
      when(commonChatService.getMember(1L, otherUserEmail)).thenReturn(otherMember);

      ChatMessageException exception = assertThrows(ChatMessageException.class,
          () -> chatMessageService.deleteMessage(1L, 1L, otherUserEmail));

      assertEquals("Только автор, модератор или админ могут удалить сообщение",
          exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMessageNotInChat() {
      Chat otherChat = new Chat();
      otherChat.setId(2L);
      testMessage.setChat(otherChat);

      when(chatMessageDao.find(1L)).thenReturn(Optional.of(testMessage));

      assertThrows(ChatMessageException.class,
          () -> chatMessageService.deleteMessage(1L, 1L, "user@test.com"));
    }
  }
}