package ru.senla.socialnetwork.unit.services.chats;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.dao.chats.MessageDao;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.exceptions.chats.MessageException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.Message;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.impl.MessageServiceImpl;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

  @Mock
  private MessageDao messageDao;

  @InjectMocks
  private MessageServiceImpl messageService;

  private ChatMember testMember;
  private Chat testChat;
  private Message testMessage;
  private MessageRequestDTO testRequest;

  @BeforeEach
  void setUp() {
    testChat = Chat.builder()
        .id(TEST_CHAT_ID)
        .name("Test Chat")
        .build();

    User testUser = User.builder()
        .id(1L)
        .email("test@example.com")
        .build();

    testMember = ChatMember.builder()
        .id(1L)
        .chat(testChat)
        .user(testUser)
        .build();

    testMessage = Message.builder()
        .id(TEST_MESSAGE_ID)
        .chat(testChat)
        .author(testUser)
        .body(TEST_BODY)
        .createdAt(ZonedDateTime.now())
        .isPinned(false)
        .build();

    testRequest = new MessageRequestDTO(TEST_BODY, null);
  }

  @Nested
  class SendMessageTests {
    @Test
    void send_whenValidMessage_thenReturnSavedMessage() {
      when(messageDao.saveOrUpdate(any(Message.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      Message result = messageService.send(testMember, testRequest, testChat);

      assertThat(result).isNotNull();
      assertThat(result.getBody()).isEqualTo(TEST_BODY);
      assertThat(result.getChat()).isEqualTo(testChat);
      assertThat(result.getAuthor()).isEqualTo(testMember.getUser());
      verify(messageDao).saveOrUpdate(any(Message.class));
    }

    @Test
    void send_whenMuted_thenThrowException() {
      testMember.setMutedUntil(ZonedDateTime.now().plusHours(1));

      assertThatThrownBy(() -> messageService.send(testMember, testRequest, testChat))
          .isInstanceOf(MessageException.class)
          .hasMessageContaining("Вы замьючены до");
    }

    @Test
    void send_whenReplyToExists_thenSetReplyTo() {
      Message replyToMessage = Message.builder()
          .id(TEST_REPLY_TO_ID)
          .chat(testChat)
          .build();

      MessageRequestDTO replyRequest = new MessageRequestDTO(TEST_BODY, TEST_REPLY_TO_ID);

      when(messageDao.findByIdAndChatId(TEST_REPLY_TO_ID, TEST_CHAT_ID))
          .thenReturn(Optional.of(replyToMessage));
      when(messageDao.saveOrUpdate(any(Message.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      Message result = messageService.send(testMember, replyRequest, testChat);

      assertThat(result.getReplyTo()).isEqualTo(replyToMessage);
      verify(messageDao).findByIdAndChatId(TEST_REPLY_TO_ID, TEST_CHAT_ID);
    }

    @Test
    void send_whenReplyToNotFound_thenThrowException() {
      MessageRequestDTO replyRequest = new MessageRequestDTO(TEST_BODY, TEST_REPLY_TO_ID);

      when(messageDao.findByIdAndChatId(TEST_REPLY_TO_ID, TEST_CHAT_ID))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> messageService.send(testMember, replyRequest, testChat))
          .isInstanceOf(MessageException.class)
          .hasMessageContaining("Сообщение для ответа не найдено");
    }
  }

  @Nested
  class GetMessagesTests {
    @Test
    void getAll_whenMessagesExist_thenReturnMessages() {
      List<Message> expected = List.of(testMessage);
      when(messageDao.findByChatId(TEST_CHAT_ID)).thenReturn(expected);

      List<Message> result = messageService.getAll(TEST_CHAT_ID);

      assertThat(result).isEqualTo(expected);
      verify(messageDao).findByChatId(TEST_CHAT_ID);
    }

    @Test
    void get_whenMessageExists_thenReturnMessage() {
      when(messageDao.find(TEST_MESSAGE_ID)).thenReturn(Optional.of(testMessage));

      Message result = messageService.get(TEST_CHAT_ID, TEST_MESSAGE_ID);

      assertThat(result).isEqualTo(testMessage);
      verify(messageDao).find(TEST_MESSAGE_ID);
    }

    @Test
    void get_whenMessageNotExists_thenThrowException() {
      when(messageDao.find(TEST_MESSAGE_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> messageService.get(TEST_CHAT_ID, TEST_MESSAGE_ID))
          .isInstanceOf(MessageException.class)
          .hasMessageContaining("Сообщение не найдено");
    }
  }

  @Nested
  class AnswersTests {
    @Test
    void getAnswers_whenAnswersExist_thenReturnMessages() {
      List<Message> expected = List.of(testMessage);
      when(messageDao.findAnswers(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(expected);

      List<Message> result = messageService.getAnswers(TEST_CHAT_ID, TEST_MESSAGE_ID);

      assertThat(result).isEqualTo(expected);
      verify(messageDao).findAnswers(TEST_CHAT_ID, TEST_MESSAGE_ID);
    }
  }

  @Nested
  class PinTests {
    @Test
    void pin_whenNotPinned_thenPinMessage() {
      when(messageDao.find(TEST_MESSAGE_ID)).thenReturn(Optional.of(testMessage));
      when(messageDao.saveOrUpdate(any(Message.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      Message result = messageService.pin(TEST_CHAT_ID, TEST_MESSAGE_ID);

      assertThat(result.getIsPinned()).isTrue();
      verify(messageDao).saveOrUpdate(testMessage);
    }

    @Test
    void pin_whenAlreadyPinned_thenThrowException() {
      testMessage.setIsPinned(true);
      when(messageDao.find(TEST_MESSAGE_ID)).thenReturn(Optional.of(testMessage));

      assertThatThrownBy(() -> messageService.pin(TEST_CHAT_ID, TEST_MESSAGE_ID))
          .isInstanceOf(MessageException.class)
          .hasMessageContaining("уже закреплено");
    }

    @Test
    void unpin_whenPinned_thenUnpinMessage() {
      testMessage.setIsPinned(true);
      when(messageDao.find(TEST_MESSAGE_ID)).thenReturn(Optional.of(testMessage));
      when(messageDao.saveOrUpdate(any(Message.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      Message result = messageService.unpin(TEST_CHAT_ID, TEST_MESSAGE_ID);

      assertThat(result.getIsPinned()).isFalse();
      verify(messageDao).saveOrUpdate(testMessage);
    }

    @Test
    void unpin_whenNotPinned_thenThrowException() {
      when(messageDao.find(TEST_MESSAGE_ID)).thenReturn(Optional.of(testMessage));

      assertThatThrownBy(() -> messageService.unpin(TEST_CHAT_ID, TEST_MESSAGE_ID))
          .isInstanceOf(MessageException.class)
          .hasMessageContaining("не закреплено");
    }
  }

  @Nested
  class PinnedMessagesTests {
    @Test
    void getPinned_whenPinnedExist_thenReturnMessages() {
      testMessage.setIsPinned(true);
      List<Message> expected = List.of(testMessage);
      when(messageDao.findPinnedByChatId(TEST_CHAT_ID)).thenReturn(expected);

      List<Message> result = messageService.getPinned(TEST_CHAT_ID);

      assertThat(result).isEqualTo(expected);
      verify(messageDao).findPinnedByChatId(TEST_CHAT_ID);
    }

    @Test
    void getPinned_whenNoPinned_thenThrowException() {
      when(messageDao.findPinnedByChatId(TEST_CHAT_ID)).thenReturn(List.of());

      assertThatThrownBy(() -> messageService.getPinned(TEST_CHAT_ID))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("нет закреплённых сообщений");
    }
  }
}