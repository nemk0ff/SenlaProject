package ru.senla.socialnetwork.unit.controllers.chats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.senla.socialnetwork.controllers.chats.impl.MessageControllerImpl;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.dto.chats.MessageResponseDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;

import java.util.List;

import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.facades.chats.MessageFacade;

@ExtendWith(MockitoExtension.class)
class MessageControllerImplTest {

  @Mock
  private MessageFacade messageFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private MessageControllerImpl messageController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private MessageResponseDTO testMessage;
  private MessageRequestDTO testMessageRequest;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(messageController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();

    testMessage = new MessageResponseDTO(
        TEST_MESSAGE_ID,
        TEST_CHAT_ID,
        TEST_BODY,
        TEST_EMAIL_1,
        TEST_DATE,
        null,
        false
    );

    testMessageRequest = new MessageRequestDTO(TEST_BODY, null);
  }

  @Nested
  class SendMessageTests {
    @Test
    void sendMessage_shouldReturnCreatedMessage() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(messageFacade.send(eq(TEST_CHAT_ID), eq(TEST_EMAIL_1), any(MessageRequestDTO.class)))
          .thenReturn(testMessage);

      mockMvc.perform(post("/chats/{chatId}/messages", TEST_CHAT_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testMessageRequest))
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_MESSAGE_ID))
          .andExpect(jsonPath("$.body").value(TEST_BODY));
    }

    @Test
    void sendMessage_shouldReturnBadRequestForEmptyBody() throws Exception {
      MessageRequestDTO invalidRequest = new MessageRequestDTO("", null);

      mockMvc.perform(post("/chats/{chatId}/messages", TEST_CHAT_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class GetMessagesTests {
    @Test
    void getMessages_shouldReturnMessagesList() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(messageFacade.getAll(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(List.of(testMessage));

      mockMvc.perform(get("/chats/{chatId}/messages", TEST_CHAT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_MESSAGE_ID))
          .andExpect(jsonPath("$[0].chatId").value(TEST_CHAT_ID));
    }
  }

  @Nested
  class GetMessageTests {
    @Test
    void getMessage_shouldReturnMessage() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(messageFacade.get(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1))
          .thenReturn(testMessage);

      mockMvc.perform(get("/chats/{chatId}/messages/{messageId}", TEST_CHAT_ID, TEST_MESSAGE_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_MESSAGE_ID));
    }
  }

  @Nested
  class GetAnswersTests {
    @Test
    void getAnswers_shouldReturnAnswersList() throws Exception {
      MessageResponseDTO answer = new MessageResponseDTO(
          TEST_MESSAGE_ID + 1,
          TEST_CHAT_ID,
          "Ответ на сообщение",
          TEST_EMAIL_2,
          TEST_DATE,
          TEST_MESSAGE_ID,
          false
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(messageFacade.getAnswers(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1))
          .thenReturn(List.of(answer));

      mockMvc.perform(get("/chats/{chatId}/messages/{messageId}/answers", TEST_CHAT_ID, TEST_MESSAGE_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].replyToId").value(TEST_MESSAGE_ID));
    }
  }

  @Nested
  class GetPinnedMessagesTests {
    @Test
    void getPinnedMessages_shouldReturnPinnedMessages() throws Exception {
      MessageResponseDTO pinnedMessage = new MessageResponseDTO(
          TEST_MESSAGE_ID,
          TEST_CHAT_ID,
          TEST_BODY,
          TEST_EMAIL_1,
          TEST_DATE,
          null,
          true
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(messageFacade.getPinned(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(List.of(pinnedMessage));

      mockMvc.perform(get("/chats/{chatId}/messages/pinned", TEST_CHAT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].isPinned").value(true));
    }
  }

  @Nested
  class PinMessageTests {
    @Test
    void pinMessage_shouldReturnPinnedMessage() throws Exception {
      MessageResponseDTO pinnedMessage = new MessageResponseDTO(
          TEST_MESSAGE_ID,
          TEST_CHAT_ID,
          TEST_BODY,
          TEST_EMAIL_1,
          TEST_DATE,
          null,
          true
      );

      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(messageFacade.pin(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1))
          .thenReturn(pinnedMessage);

      mockMvc.perform(patch("/chats/{chatId}/messages/{messageId}/pin", TEST_CHAT_ID, TEST_MESSAGE_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.isPinned").value(true));
    }
  }

  @Nested
  class UnpinMessageTests {
    @Test
    void unpinMessage_shouldReturnUnpinnedMessage() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(messageFacade.unpin(TEST_CHAT_ID, TEST_MESSAGE_ID, TEST_EMAIL_1))
          .thenReturn(testMessage);

      mockMvc.perform(delete("/chats/{chatId}/messages/{messageId}/pin", TEST_CHAT_ID, TEST_MESSAGE_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.isPinned").value(false));
    }
  }

  @Nested
  class DeleteMessageTests {
    @Test
    void deleteMessage_shouldReturnSuccessResponse() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);

      mockMvc.perform(delete("/chats/{chatId}/messages/{messageId}", TEST_CHAT_ID, TEST_MESSAGE_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Сообщение успешно удалено"))
          .andExpect(jsonPath("$.data.chatId").value(TEST_CHAT_ID))
          .andExpect(jsonPath("$.data.messageId").value(TEST_MESSAGE_ID));
    }
  }
}