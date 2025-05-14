package ru.senla.socialnetwork.services.chats;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static ru.senla.socialnetwork.TestConstants.*;
import ru.senla.socialnetwork.dao.chats.ChatMemberDao;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.chats.Chat;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.impl.ChatMemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class ChatMemberServiceImplTest {

  @Mock
  private ChatMemberDao chatMemberDao;

  @InjectMocks
  private ChatMemberServiceImpl chatMemberService;

  private Chat testChat;
  private User testUser;
  private ChatMember testMember;

  @BeforeEach
  void setUp() {
    testChat = Chat.builder()
        .id(TEST_CHAT_ID)
        .name(TEST_CHAT_NAME)
        .isGroup(true)
        .createdAt(TEST_DATE)
        .build();

    testUser = User.builder()
        .id(TEST_USER_ID)
        .email(TEST_EMAIL_1)
        .name(TEST_NAME)
        .build();

    testMember = ChatMember.builder()
        .id(1L)
        .chat(testChat)
        .user(testUser)
        .role(MemberRole.MEMBER)
        .joinDate(TEST_DATE)
        .build();
  }

  @Nested
  class AddUserToChatTests {
    @Test
    void addUserToChat_whenValidGroupChat_thenReturnMember() {
      when(chatMemberDao.countByChatId(TEST_CHAT_ID)).thenReturn(5L);
      when(chatMemberDao.saveOrUpdate(any(ChatMember.class))).thenReturn(testMember);

      ChatMember result = chatMemberService.addUserToChat(testChat, testUser);

      assertThat(result).isEqualTo(testMember);
      verify(chatMemberDao).countByChatId(TEST_CHAT_ID);
      verify(chatMemberDao).saveOrUpdate(any(ChatMember.class));
    }

    @Test
    void addUserToChat_whenPrivateChat_thenThrowException() {
      Chat privateChat = Chat.builder().isGroup(false).build();

      assertThatThrownBy(() -> chatMemberService.addUserToChat(privateChat, testUser))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Нельзя добавить участника в личный чат");
    }

    @Test
    void addUserToChat_whenChatFull_thenThrowException() {
      when(chatMemberDao.countByChatId(TEST_CHAT_ID))
          .thenReturn((long) ChatMemberServiceImpl.MAX_CHAT_SIZE);

      assertThatThrownBy(() -> chatMemberService.addUserToChat(testChat, testUser))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Превышено максимальное количество участников");
    }
  }

  @Nested
  class MuteTests {
    @Test
    void mute_whenValidMember_thenReturnMutedMember() {
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));
      when(chatMemberDao.saveOrUpdate(any(ChatMember.class))).thenReturn(testMember);

      ChatMember result = chatMemberService.mute(TEST_CHAT_ID, TEST_EMAIL_1, TEST_FUTURE_DATE);

      assertThat(result.getMutedUntil()).isEqualTo(TEST_FUTURE_DATE);
      verify(chatMemberDao).saveOrUpdate(testMember);
    }

    @Test
    void mute_whenNotMember_thenThrowException() {
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> chatMemberService.mute(TEST_CHAT_ID, TEST_EMAIL_1, TEST_FUTURE_DATE))
          .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void mute_whenAdmin_thenThrowException() {
      testMember.setRole(MemberRole.ADMIN);
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      assertThatThrownBy(() -> chatMemberService.mute(TEST_CHAT_ID, TEST_EMAIL_1, TEST_FUTURE_DATE))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Можно мьютить только обычных участников");
    }
  }

  @Nested
  class UnmuteTests {
    @Test
    void unmute_whenMuted_thenReturnUnmutedMember() {
      ChatMember mutedMember = ChatMember.builder()
          .id(1L)
          .chat(testChat)
          .user(testUser)
          .role(MemberRole.MEMBER)
          .joinDate(ZonedDateTime.now().minusDays(1))
          .mutedUntil(TEST_FUTURE_DATE)
          .build();
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(mutedMember));
      when(chatMemberDao.saveOrUpdate(any(ChatMember.class)))
          .thenAnswer(invocation -> {
            ChatMember member = invocation.getArgument(0);
            member.setMutedUntil(null);
            return member;
          });

      ChatMember result = chatMemberService.unmute(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result).isNotNull();
      assertThat(result.getMutedUntil()).isNull();
      verify(chatMemberDao).saveOrUpdate(mutedMember);
    }

    @Test
    void unmute_whenNotMuted_thenThrowException() {
      ChatMember notMutedMember = ChatMember.builder()
          .id(1L)
          .chat(testChat)
          .user(testUser)
          .role(MemberRole.MEMBER)
          .joinDate(ZonedDateTime.now().minusDays(1))
          .mutedUntil(null)
          .build();

      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(notMutedMember));

      assertThatThrownBy(() -> chatMemberService.unmute(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Пользователь не является замьюченным");
    }
  }

  @Nested
  class LeaveTests {
    @Test
    void leave_whenValidMember_thenReturnMemberWithLeaveDate() {
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));
      when(chatMemberDao.saveOrUpdate(any(ChatMember.class))).thenReturn(testMember);

      ChatMember result = chatMemberService.leave(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result.getLeaveDate()).isNotNull();
      verify(chatMemberDao).saveOrUpdate(testMember);
    }

    @Test
    void leave_whenLastAdmin_thenThrowException() {
      testMember.setRole(MemberRole.ADMIN);
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));
      when(chatMemberDao.countByChatIdAndRole(TEST_CHAT_ID, MemberRole.ADMIN))
          .thenReturn(1L);

      assertThatThrownBy(() -> chatMemberService.leave(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("Нельзя покинуть чат, так как вы единственный админ");
    }
  }

  @Nested
  class ChangeRoleTests {
    @Test
    void changeRole_toModerator_whenValidConditions_thenSuccess() {
      ChatMember memberToChange = ChatMember.builder()
          .id(1L)
          .chat(testChat)
          .user(testUser)
          .role(MemberRole.MEMBER)
          .joinDate(ZonedDateTime.now().minusDays(1))
          .build();
      when(chatMemberDao.countByChatIdAndRole(TEST_CHAT_ID, MemberRole.MODERATOR))
          .thenReturn(0L);
      when(chatMemberDao.saveOrUpdate(any(ChatMember.class)))
          .thenAnswer(invocation -> {
            ChatMember m = invocation.getArgument(0);
            m.setRole(MemberRole.MODERATOR);
            return m;
          });

      ChatMember result = chatMemberService.changeRole(
          TEST_CHAT_ID, memberToChange, MemberRole.MODERATOR);

      assertThat(result).isNotNull();
      assertThat(result.getRole()).isEqualTo(MemberRole.MODERATOR);
      verify(chatMemberDao).countByChatIdAndRole(TEST_CHAT_ID, MemberRole.MODERATOR);
      verify(chatMemberDao).saveOrUpdate(memberToChange);
      verify(chatMemberDao, never())
          .countByChatIdAndRole(TEST_CHAT_ID, MemberRole.ADMIN);
    }

    @Test
    void changeRole_toAdmin_whenValidConditions_thenSuccess() {
      ChatMember memberToChange = ChatMember.builder()
          .id(1L)
          .chat(testChat)
          .user(testUser)
          .role(MemberRole.MODERATOR)
          .joinDate(ZonedDateTime.now().minusDays(1))
          .build();
      when(chatMemberDao.countByChatIdAndRole(TEST_CHAT_ID, MemberRole.ADMIN))
          .thenReturn(1L);
      when(chatMemberDao.saveOrUpdate(any(ChatMember.class)))
          .thenAnswer(invocation -> {
            ChatMember m = invocation.getArgument(0);
            m.setRole(MemberRole.ADMIN);
            return m;
          });

      ChatMember result = chatMemberService.changeRole(
          TEST_CHAT_ID, memberToChange, MemberRole.ADMIN);

      assertThat(result.getRole()).isEqualTo(MemberRole.ADMIN);
      verify(chatMemberDao).countByChatIdAndRole(TEST_CHAT_ID, MemberRole.ADMIN);
      verify(chatMemberDao).saveOrUpdate(memberToChange);
    }

    @Test
    void changeRole_whenTooManyAdmins_thenThrowException() {
      when(chatMemberDao.countByChatIdAndRole(TEST_CHAT_ID, MemberRole.ADMIN))
          .thenReturn((long) (ChatMemberServiceImpl.MAX_ADMINS_NUMBER + 1));

      assertThatThrownBy(() -> chatMemberService.changeRole(
          TEST_CHAT_ID, testMember, MemberRole.ADMIN))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("В чате слишком много админов");
    }

    @Test
    void changeRole_whenTooManyModerators_thenThrowException() {
      when(chatMemberDao.countByChatIdAndRole(TEST_CHAT_ID, MemberRole.MODERATOR))
          .thenReturn((long) (ChatMemberServiceImpl.MAX_MODERATORS_NUMBER + 1));

      assertThatThrownBy(() -> chatMemberService.changeRole(
          TEST_CHAT_ID, testMember, MemberRole.MODERATOR))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("В чате слишком много модераторов");
    }
  }

  @Nested
  class GetMemberTests {
    @Test
    void getMember_whenExists_thenReturnMember() {
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      ChatMember result = chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result).isEqualTo(testMember);
    }

    @Test
    void getMember_whenNotExists_thenThrowException() {
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMember_whenLeftChat_thenThrowException() {
      testMember.setJoinDate(ZonedDateTime.now().minusDays(2));
      testMember.setLeaveDate(ZonedDateTime.now().minusDays(1));

      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      assertThatThrownBy(() -> chatMemberService.getMember(TEST_CHAT_ID, TEST_EMAIL_1))
          .isInstanceOf(ChatMemberException.class)
          .hasMessageContaining("не является участником чата");
    }
  }

  @Nested
  class OtherMethodsTests {
    @Test
    void getMembers_whenCalled_thenReturnMembers() {
      List<ChatMember> expected = List.of(testMember);
      when(chatMemberDao.findAllByChatId(TEST_CHAT_ID))
          .thenReturn(expected);

      List<ChatMember> result = chatMemberService.getMembers(TEST_CHAT_ID);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    void isChatMember_whenActiveMember_thenReturnTrue() {
      when(chatMemberDao.findActiveByChatIdAndUserEmail(TEST_CHAT_ID, TEST_EMAIL_1))
          .thenReturn(Optional.of(testMember));

      boolean result = chatMemberService.isChatMember(TEST_CHAT_ID, TEST_EMAIL_1);

      assertThat(result).isTrue();
    }

    @Test
    void removeMember_whenCalled_thenSetLeaveDate() {
      ChatMember memberToRemove = ChatMember.builder()
          .id(1L)
          .chat(testChat)
          .user(testUser)
          .role(MemberRole.MEMBER)
          .joinDate(ZonedDateTime.now().minusDays(1))
          .build();

      when(chatMemberDao.saveOrUpdate(any(ChatMember.class)))
          .thenAnswer(invocation -> {
            ChatMember member = invocation.getArgument(0);
            member.setLeaveDate(ZonedDateTime.now());
            return member;
          });

      ChatMember result = chatMemberService.removeMember(memberToRemove);

      assertThat(result).isNotNull();
      assertThat(result.getLeaveDate()).isNotNull();
      assertThat(result.getLeaveDate()).isAfterOrEqualTo(memberToRemove.getJoinDate());
      verify(chatMemberDao).saveOrUpdate(memberToRemove);
    }

    @Test
    void recreate_whenCalled_thenUpdateJoinDate() {
      ChatMember memberToRecreate = ChatMember.builder()
          .id(1L)
          .chat(testChat)
          .user(testUser)
          .role(MemberRole.MEMBER)
          .joinDate(TEST_DATE.minusDays(1))
          .build();

      when(chatMemberDao.saveOrUpdate(any(ChatMember.class)))
          .thenAnswer(obj -> obj.getArgument(0));

      ZonedDateTime beforeCall = ZonedDateTime.now();
      ChatMember result = chatMemberService.recreate(memberToRecreate);
      ZonedDateTime afterCall = ZonedDateTime.now();

      assertThat(result).isNotNull();
      assertThat(result.getJoinDate())
          .isAfterOrEqualTo(beforeCall)
          .isBeforeOrEqualTo(afterCall);
      verify(chatMemberDao).saveOrUpdate(memberToRecreate);
    }
  }
}