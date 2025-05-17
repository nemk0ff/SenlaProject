package ru.senla.socialnetwork.facades.chats;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.senla.socialnetwork.TestConstants.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.dto.chats.MessageResponseDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMessageMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.exceptions.chats.MessageException;
import ru.senla.socialnetwork.facades.chats.impl.MessageFacadeImpl;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.Message;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.MessageService;
import ru.senla.socialnetwork.services.chats.impl.ChatServiceImpl;

@ExtendWith(MockitoExtension.class)
class MessageFacadeImplTest {
  @Mock
  private ChatMessageMapper chatMessageMapper;
  @Mock
  private ChatMemberService chatMemberService;
  @Mock
  private MessageService messageService;
  @Mock
  private ChatServiceImpl chatServiceImpl;

  @InjectMocks
  private MessageFacadeImpl messageFacade;

  private User testUser;
  private Chat testChat;
  private ChatMember testMember;
  private Message testMessage;
  private MessageResponseDTO testMessageDTO;
  private MessageRequestDTO testRequestDTO;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(TEST_USER_ID_1)
        .email(TEST_EMAIL_1)
        .build();

    testChat = Chat.builder()
        .id(TEST_CHAT_ID)
        .name(TEST_CHAT_NAME)
        .isGroup(true)
        .build();

    testMember = ChatMember.builder()
        .id(1L)
        .user(testUser)
        .chat(testChat)
        .role(MemberRole.ADMIN)
        .build();

    testMessage = Message.builder()
        .id(TEST_MESSAGE_ID)
        .author(testUser)
        .chat(testChat)
        .body(TEST_BODY)
        .createdAt(TEST_DATE)
        .isPinned(false)
        .build();

    testMessageDTO = new MessageResponseDTO(
        TEST_MESSAGE_ID,
        TEST_CHAT_ID,
        TEST_BODY,
        TEST_EMAIL_1,
        TEST_DATE,
        null,
        false);

    testRequestDTO = new MessageRequestDTO(TEST_BODY, null);
  }

  @Nested
  class SendTests {
    @Test
    void send_whenValidRequest_thenReturnMessageDTO() {
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember);
      when(chatServiceImpl.get(TEST_CHAT_ID)).thenReturn(testChat);
      when(messageService.send(testMember, testRequestDTO, testChat)).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(testMessageDTO);

      MessageResponseDTO result = messageFacade.send(TEST_CHAT_ID, TEST_EMAIL_1, testRequestDTO);

      assertThat(result).isEqualTo(testMessageDTO);
      verify(chatMemberService).getMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(chatServiceImpl).get(TEST_CHAT_ID);
      verify(messageService).send(testMember, testRequestDTO, testChat);
    }
  }

  @Nested
  class GetAllTests {
    @Test
    void getAll_whenMember_thenReturnMessages() {
      List<Message> messages = List.of(testMessage);
      List<MessageResponseDTO> expectedDTOs = List.of(testMessageDTO);

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(messageService.getAll(TEST_CHAT_ID)).thenReturn(messages);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(testMessageDTO);

      List<MessageResponseDTO> result = messageFacade.getAll(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(expectedDTOs);
      verify(chatMemberService).isChatMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(messageService).getAll(TEST_CHAT_ID);
    }

    @Test
    void getAll_whenNotMember_thenThrowException() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(false);

      assertThatThrownBy(() -> messageFacade.getAll(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(MessageException.class)
          .hasMessageContaining("Недостаточно прав для выполнения этой операции");
    }
  }

  @Nested
  class GetTests {
    @Test
    void get_whenMember_thenReturnMessage() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(messageService.get(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(testMessageDTO);

      MessageResponseDTO result = messageFacade.get(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMessageDTO);
      verify(chatMemberService).isChatMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(messageService).get(TEST_CHAT_ID, TEST_MESSAGE_ID);
    }

    @Test
    void get_whenNotMember_thenThrowException() {
      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(false);

      assertThatThrownBy(() -> messageFacade.get(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1))
          .isInstanceOf(MessageException.class)
          .hasMessageContaining("Недостаточно прав для выполнения этой операции");
    }
  }

  @Nested
  class GetAnswersTests {
    @Test
    void getAnswers_whenMember_thenReturnAnswers() {
      Message answerMessage = Message.builder()
          .id(2L)
          .author(testUser)
          .chat(testChat)
          .body("Answer")
          .replyTo(testMessage)
          .createdAt(TEST_DATE)
          .build();

      MessageResponseDTO answerDTO = new MessageResponseDTO(
          2L,
          TEST_CHAT_ID,
          "Answer",
          TEST_EMAIL_1,
          TEST_DATE,
          TEST_MESSAGE_ID,
          false);

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(messageService.getAnswers(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(List.of(answerMessage));
      when(chatMessageMapper.toDTO(answerMessage)).thenReturn(answerDTO);

      List<MessageResponseDTO> result = messageFacade.getAnswers(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).replyToId()).isEqualTo(TEST_MESSAGE_ID);
      verify(chatMemberService).isChatMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(messageService).getAnswers(TEST_CHAT_ID, TEST_MESSAGE_ID);
    }
  }

  @Nested
  class GetPinnedTests {
    @Test
    void getPinned_whenMember_thenReturnPinnedMessages() {
      Message pinnedMessage = Message.builder()
          .id(2L)
          .author(testUser)
          .chat(testChat)
          .body("Pinned")
          .isPinned(true)
          .createdAt(TEST_DATE)
          .build();

      MessageResponseDTO pinnedDTO = new MessageResponseDTO(
          2L,
          TEST_CHAT_ID,
          "Pinned",
          TEST_EMAIL_1,
          TEST_DATE,
          null,
          true);

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(messageService.getPinned(TEST_CHAT_ID)).thenReturn(List.of(pinnedMessage));
      when(chatMessageMapper.toDTO(pinnedMessage)).thenReturn(pinnedDTO);

      List<MessageResponseDTO> result = messageFacade.getPinned(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).isPinned()).isTrue();
      verify(chatMemberService).isChatMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(messageService).getPinned(TEST_CHAT_ID);
    }
  }

  @Nested
  class PinUnpinTests {
    @Test
    void pin_whenAdmin_thenPinMessage() {
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember);
      when(messageService.pin(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(testMessageDTO);

      MessageResponseDTO result = messageFacade.pin(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMessageDTO);
      verify(chatMemberService).getMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(messageService).pin(TEST_CHAT_ID, TEST_MESSAGE_ID);
    }

    @Test
    void pin_whenMember_thenThrowException() {
      ChatMember member = ChatMember.builder()
          .user(testUser)
          .role(MemberRole.MEMBER)
          .build();

      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(member);

      assertThatThrownBy(() -> messageFacade.pin(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Недостаточно прав для выполнения этой операции");
    }

    @Test
    void unpin_whenAdmin_thenUnpinMessage() {
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember);
      when(messageService.unpin(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(testMessage);
      when(chatMessageMapper.toDTO(testMessage)).thenReturn(testMessageDTO);

      MessageResponseDTO result = messageFacade.unpin(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMessageDTO);
      verify(chatMemberService).getMember(TEST_CHAT_ID, TEST_EMAIL_1);
      verify(messageService).unpin(TEST_CHAT_ID, TEST_MESSAGE_ID);
    }
  }

  @Nested
  class DeleteTests {
    @Test
    void delete_whenAuthor_thenDeleteMessage() {
      Message messageToDelete = Message.builder()
          .id(TEST_MESSAGE_ID)
          .author(testUser)
          .chat(testChat)
          .build();

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(messageService.get(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(messageToDelete);

      messageFacade.delete(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1);

      verify(messageService).delete(messageToDelete);
    }

    @Test
    void delete_whenAdmin_thenDeleteMessage() {
      User otherUser = User.builder().email("other@email.com").build();
      Message messageToDelete = Message.builder()
          .id(TEST_MESSAGE_ID)
          .author(otherUser)
          .chat(testChat)
          .build();

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(messageService.get(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(messageToDelete);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(testMember);

      messageFacade.delete(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1);

      verify(messageService).delete(messageToDelete);
    }

    @Test
    void delete_whenNotAuthorNorAdmin_thenThrowException() {
      User otherUser = User.builder().email("other@email.com").build();
      Message messageToDelete = Message.builder()
          .id(TEST_MESSAGE_ID)
          .author(otherUser)
          .chat(testChat)
          .build();

      ChatMember regularMember = ChatMember.builder()
          .user(testUser)
          .role(MemberRole.MEMBER)
          .build();

      when(chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(true);
      when(messageService.get(TEST_CHAT_ID, TEST_MESSAGE_ID)).thenReturn(messageToDelete);
      when(chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1)).thenReturn(regularMember);

      assertThatThrownBy(() -> messageFacade.delete(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Недостаточно прав для выполнения этой операции");
    }
  }
}