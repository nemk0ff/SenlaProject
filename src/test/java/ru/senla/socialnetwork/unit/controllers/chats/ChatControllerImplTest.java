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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.senla.socialnetwork.controllers.chats.impl.ChatControllerImpl;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.facades.chats.ChatFacade;

import java.util.List;
import java.util.Set;

import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.model.MemberRole;

@ExtendWith(MockitoExtension.class)
class ChatControllerImplTest {

  @Mock
  private ChatFacade chatFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private ChatControllerImpl chatController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private ChatDTO testChat;
  private CreateGroupChatDTO testGroupChatRequest;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    mockMvc = MockMvcBuilders.standaloneSetup(chatController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .build();

    testChat = new ChatDTO(
        TEST_CHAT_ID,
        TEST_CHAT_NAME,
        true,
        TEST_DATE,
        List.of(new ChatMemberDTO(TEST_EMAIL_1, TEST_CHAT_ID, TEST_CHAT_NAME, MemberRole.MEMBER,
            TEST_DATE, null))
    );

    testGroupChatRequest = new CreateGroupChatDTO(
        TEST_CHAT_NAME,
        Set.of(TEST_EMAIL_1, TEST_EMAIL_2)
    );
  }

  @Nested
  class GetUserChatsTests {
    @Test
    void getUserChats_shouldReturnChatsList() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(chatFacade.getUserChats(TEST_EMAIL_1))
          .thenReturn(List.of(testChat));

      mockMvc.perform(get("/chats")
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(TEST_CHAT_ID))
          .andExpect(jsonPath("$[0].name").value(TEST_CHAT_NAME));
    }
  }

  @Nested
  class CreateGroupChatTests {
    @Test
    void createGroupChat_shouldReturnCreatedChat() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(chatFacade.create(any(CreateGroupChatDTO.class), eq(TEST_EMAIL_1)))
          .thenReturn(testChat);

      mockMvc.perform(post("/chats/group")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(testGroupChatRequest))
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_CHAT_ID));
    }

    @Test
    void createGroupChat_shouldReturnBadRequestForInvalidInput() throws Exception {
      CreateGroupChatDTO invalidRequest = new CreateGroupChatDTO("", Set.of("invalid-email"));

      mockMvc.perform(post("/chats/group")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class CreatePersonalChatTests {
    @Test
    void createPersonalChat_shouldReturnCreatedChat() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(chatFacade.create(TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(testChat);

      mockMvc.perform(post("/chats/personal")
              .param("participant", TEST_EMAIL_2)
              .principal(authentication))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(TEST_CHAT_ID));
    }
  }

  @Nested
  class DeleteChatTests {
    @Test
    void deleteChat_shouldReturnSuccessMessage() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);

      mockMvc.perform(delete("/chats/" + TEST_CHAT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Чат успешно удалён"))
          .andExpect(jsonPath("$.data.chatId").value(TEST_CHAT_ID));
    }
  }

  @Nested
  class GetChatTests {
    @Test
    void getChat_shouldReturnChatInfo() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(chatFacade.get(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(testChat);

      mockMvc.perform(get("/chats/" + TEST_CHAT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(TEST_CHAT_ID))
          .andExpect(jsonPath("$.isGroup").value(true));
    }
  }
}