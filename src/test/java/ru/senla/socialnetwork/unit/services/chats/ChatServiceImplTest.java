package ru.senla.socialnetwork.unit.services.chats;

import jakarta.persistence.EntityNotFoundException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static ru.senla.socialnetwork.unit.TestConstants.*;
import ru.senla.socialnetwork.dao.chats.ChatDao;
import ru.senla.socialnetwork.dto.chats.CreateGroupChatDTO;
import ru.senla.socialnetwork.exceptions.chats.ChatException;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.services.chats.impl.ChatServiceImpl;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {
  @Mock
  private ChatDao chatDao;

  @InjectMocks
  private ChatServiceImpl chatService;

  private Chat testChat;
  private CreateGroupChatDTO groupChatDTO;

  @BeforeEach
  void setUp() {
    testChat = Chat.builder()
        .id(TEST_CHAT_ID)
        .name(TEST_CHAT_NAME)
        .createdAt(TEST_DATE)
        .build();

    groupChatDTO = new CreateGroupChatDTO(
        TEST_CHAT_NAME,
        Set.of(TEST_EMAIL_1, TEST_EMAIL_2)
    );
  }

  @Nested
  class GetAllByUserTests {
    @Test
    void getAllByUser_whenUserExists_thenReturnChats() {
      List<Chat> expectedChats = List.of(testChat);
      when(chatDao.findAllChatsByUserId(TEST_USER_ID_1)).thenReturn(expectedChats);

      List<Chat> result = chatService.getAllByUser(TEST_USER_ID_1);

      assertThat(result).isEqualTo(expectedChats);
      verify(chatDao).findAllChatsByUserId(TEST_USER_ID_1);
    }

    @Test
    void getAllByUser_whenNoChats_thenReturnEmptyList() {
      when(chatDao.findAllChatsByUserId(TEST_USER_ID_1)).thenReturn(List.of());

      List<Chat> result = chatService.getAllByUser(TEST_USER_ID_1);

      assertThat(result).isEmpty();
      verify(chatDao).findAllChatsByUserId(TEST_USER_ID_1);
    }
  }

  @Nested
  class CreateGroupChatTests {
    @Test
    void create_whenValidGroupChat_thenReturnCreatedChat() {
      testChat.setIsGroup(true);
      when(chatDao.saveOrUpdate(any(Chat.class))).thenReturn(testChat);

      Chat result = chatService.create(groupChatDTO);

      assertThat(result).isEqualTo(testChat);
      assertThat(result.getName()).isEqualTo(TEST_CHAT_NAME);
      assertThat(result.getIsGroup()).isTrue();
      verify(chatDao).saveOrUpdate(any(Chat.class));
    }

    @Test
    void create_whenEmptyMembers_thenThrowException() {
      CreateGroupChatDTO emptyMembersDTO = new CreateGroupChatDTO(TEST_CHAT_NAME, Set.of());

      assertThatThrownBy(() -> chatService.create(emptyMembersDTO))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("Групповой чат должен иметь хотя бы одного участника");
    }
  }

  @Nested
  class CreatePersonalChatTests {
    @Test
    void create_whenValidPersonalChat_thenReturnCreatedChat() {
      testChat.setIsGroup(false);
      when(chatDao.existsByMembers(TEST_EMAIL_1, TEST_EMAIL_2)).thenReturn(false);
      when(chatDao.saveOrUpdate(any(Chat.class))).thenReturn(testChat);

      Chat result = chatService.create(TEST_EMAIL_1, TEST_EMAIL_2, TEST_CHAT_NAME);

      assertThat(result).isEqualTo(testChat);
      assertThat(result.getName()).isEqualTo(TEST_CHAT_NAME);
      assertThat(result.getIsGroup()).isFalse();
      verify(chatDao).existsByMembers(TEST_EMAIL_1, TEST_EMAIL_2);
      verify(chatDao).saveOrUpdate(any(Chat.class));
    }

    @Test
    void create_whenChatExists_thenThrowException() {
      when(chatDao.existsByMembers(TEST_EMAIL_1, TEST_EMAIL_2)).thenReturn(true);

      assertThatThrownBy(() -> chatService.create(TEST_EMAIL_1, TEST_EMAIL_2, TEST_CHAT_NAME))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("Личный чат " + TEST_CHAT_NAME + " уже существует");
    }
  }

  @Nested
  class DeleteChatTests {
    @Test
    void delete_whenChatExists_thenCallDao() {
      chatService.delete(testChat);

      verify(chatDao).delete(testChat);
    }
  }

  @Nested
  class GetChatTests {
    @Test
    void get_whenChatExists_thenReturnChat() {
      when(chatDao.findWithMembersAndUsers(TEST_CHAT_ID)).thenReturn(Optional.of(testChat));

      Chat result = chatService.get(TEST_CHAT_ID);

      assertThat(result).isEqualTo(testChat);
      verify(chatDao).findWithMembersAndUsers(TEST_CHAT_ID);
    }

    @Test
    void get_whenChatNotExists_thenThrowException() {
      when(chatDao.findWithMembersAndUsers(TEST_CHAT_ID)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> chatService.get(TEST_CHAT_ID))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Чат не найден");
    }
  }
}