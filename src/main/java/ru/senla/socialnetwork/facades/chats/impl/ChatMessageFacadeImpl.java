package ru.senla.socialnetwork.facades.chats.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.chats.ChatMessageDTO;
import ru.senla.socialnetwork.dto.chats.CreateMessageDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMessageMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMessageException;
import ru.senla.socialnetwork.facades.chats.ChatMessageFacade;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.ChatMessage;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.ChatMessageService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatMessageFacadeImpl implements ChatMessageFacade {
  private final ChatMessageMapper chatMessageMapper;
  private final ChatMemberService chatMemberService;
  private final ChatMessageService chatMessageService;

  @Override
  public ChatMessageDTO send(Long chatId, String authorEmail, CreateMessageDTO request) {
    ChatMember member = chatMemberService.getMember(chatId, authorEmail);
    return chatMessageMapper.toDTO(chatMessageService.send(member, request));
  }

  @Override
  public List<ChatMessageDTO> getAll(Long chatId) {
    return chatMessageService.getAll(chatId).stream()
        .map(chatMessageMapper::toDTO)
        .toList();
  }

  @Override
  public ChatMessageDTO get(Long chatId, Long messageId) {
    return chatMessageMapper.toDTO(chatMessageService.get(chatId, messageId));
  }

  @Override
  public List<ChatMessageDTO> getAnswers(Long chatId, Long messageId) {
    return chatMessageService.getAnswers(chatId, messageId).stream()
        .map(chatMessageMapper::toDTO)
        .toList();
  }

  @Override
  public List<ChatMessageDTO> getPinned(Long chatId) {
    return chatMessageService.getPinned(chatId).stream()
        .map(chatMessageMapper::toDTO)
        .toList();
  }

  @Override
  public ChatMessageDTO pin(Long chatId, Long messageId) {
    return chatMessageMapper.toDTO(chatMessageService.pin(chatId, messageId));
  }

  @Override
  public ChatMessageDTO unpin(Long chatId, Long messageId) {
    return chatMessageMapper.toDTO(chatMessageService.unpin(chatId, messageId));
  }

  @Override
  public void delete(Long chatId, Long messageId, String currentUserEmail) {
    ChatMessage message = chatMessageService.get(chatId, messageId);
    ChatMember member = chatMemberService.getMember(chatId, currentUserEmail);

    if (!message.getAuthor().getEmail().equals(currentUserEmail)) {
      if (member.getRole() != MemberRole.ADMIN && member.getRole() != MemberRole.MODERATOR) {
        throw new ChatMessageException("Вы не можете удалить данное сообщение");
      }
    }
    chatMessageService.delete(message);
  }
}
