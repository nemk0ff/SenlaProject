package ru.senla.socialnetwork.controllers.chats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.senla.socialnetwork.controllers.chats.impl.ChatMemberControllerImpl;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.exceptions.RestResponseEntityExceptionHandler;
import ru.senla.socialnetwork.facades.chats.ChatMemberFacade;
import ru.senla.socialnetwork.model.MemberRole;

import java.time.ZonedDateTime;

import static ru.senla.socialnetwork.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class ChatMemberControllerImplTest {
  @Mock
  private ChatMemberFacade chatMemberFacade;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private ChatMemberControllerImpl chatMemberController;

  private MockMvc mockMvc;
  private ChatMemberDTO testMember;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(chatMemberController)
        .setControllerAdvice(new RestResponseEntityExceptionHandler())
        .build();

    testMember = new ChatMemberDTO(
        TEST_EMAIL_1,
        TEST_CHAT_ID,
        TEST_CHAT_NAME,
        MemberRole.MEMBER,
        TEST_DATE,
        null
    );
  }

  @Nested
  class AddMemberTests {
    @Test
    void addMember_shouldReturnAddedMember() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
      when(chatMemberFacade.addUserToChat(TEST_CHAT_ID, TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(testMember);

      mockMvc.perform(post("/chats/{chatId}/members", TEST_CHAT_ID)
              .param("email", TEST_EMAIL_1)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1))
          .andExpect(jsonPath("$.chatId").value(TEST_CHAT_ID));
    }
  }

  @Nested
  class RemoveMemberTests {
    @Test
    void removeMember_shouldReturnRemovedMember() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
      when(chatMemberFacade.removeUser(TEST_CHAT_ID, TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(testMember);

      mockMvc.perform(delete("/chats/{chatId}/members", TEST_CHAT_ID)
              .param("email", TEST_EMAIL_1)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class MuteMemberTests {
    @Test
    void muteMember_shouldReturnMutedMember() throws Exception {
      ZonedDateTime muteUntil = TEST_DATE.plusHours(1);
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
      when(chatMemberFacade.mute(TEST_CHAT_ID, TEST_EMAIL_1, muteUntil, TEST_EMAIL_2))
          .thenReturn(testMember);

      mockMvc.perform(post("/chats/{chatId}/members/mute", TEST_CHAT_ID)
              .param("email", TEST_EMAIL_1)
              .param("muteUntil", muteUntil.toString())
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }

    @Test
    void muteMember_shouldReturnBadRequestForInvalidDate() throws Exception {
      mockMvc.perform(post("/chats/{chatId}/members/mute", TEST_CHAT_ID)
              .param("email", TEST_EMAIL_1)
              .param("muteUntil", "invalid-date"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class UnmuteMemberTests {
    @Test
    void unmuteMember_shouldReturnUnmutedMember() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
      when(chatMemberFacade.unmute(TEST_CHAT_ID, TEST_EMAIL_1, TEST_EMAIL_2))
          .thenReturn(testMember);

      mockMvc.perform(post("/chats/{chatId}/members/unmute", TEST_CHAT_ID)
              .param("email", TEST_EMAIL_1)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class LeaveChatTests {
    @Test
    void leaveChat_shouldReturnLeftMember() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_1);
      when(chatMemberFacade.leave(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(testMember);

      mockMvc.perform(delete("/chats/{chatId}/members/leave", TEST_CHAT_ID)
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }
  }

  @Nested
  class ChangeMemberRoleTests {
    @Test
    void changeMemberRole_shouldReturnUpdatedMember() throws Exception {
      when(authentication.getName()).thenReturn(TEST_EMAIL_2);
      when(chatMemberFacade.changeRole(TEST_CHAT_ID, TEST_EMAIL_1, MemberRole.ADMIN, TEST_EMAIL_2))
          .thenReturn(testMember);

      mockMvc.perform(post("/chats/{chatId}/members/role", TEST_CHAT_ID)
              .param("email", TEST_EMAIL_1)
              .param("role", "ADMIN")
              .principal(authentication))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(TEST_EMAIL_1));
    }

    @Test
    void changeMemberRole_shouldReturnBadRequestForInvalidRole() throws Exception {
      mockMvc.perform(post("/chats/{chatId}/members/role", TEST_CHAT_ID)
              .param("email", TEST_EMAIL_1)
              .param("role", "INVALID_ROLE"))
          .andExpect(status().isBadRequest());
    }
  }
}
