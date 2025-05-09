package ru.senla.socialnetwork.facades.chats.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.chats.MessageResponseDTO;
import ru.senla.socialnetwork.dto.chats.MessageRequestDTO;
import ru.senla.socialnetwork.dto.mappers.ChatMessageMapper;
import ru.senla.socialnetwork.exceptions.chats.ChatMemberException;
import ru.senla.socialnetwork.exceptions.chats.MessageException;
import ru.senla.socialnetwork.facades.chats.MessageFacade;
import ru.senla.socialnetwork.model.chats.ChatMember;
import ru.senla.socialnetwork.model.chats.Message;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.services.chats.ChatMemberService;
import ru.senla.socialnetwork.services.chats.MessageService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessageFacadeImpl implements MessageFacade {
  private final ChatMessageMapper chatMessageMapper;
  private final ChatMemberService chatMemberService;
  private final MessageService messageService;

  @Override
  public MessageResponseDTO send(Long chatId, String authorEmail, MessageRequestDTO request) {
    ChatMember member = chatMemberService.getMember(chatId, authorEmail);
    return chatMessageMapper.toDTO(messageService.send(member, request));
  }

  @Override
  public List<MessageResponseDTO> getAll(Long chatId, String clientEmail) {
    checkIsMember(chatId, clientEmail);
    return messageService.getAll(chatId).stream()
        .map(chatMessageMapper::toDTO)
        .toList();
  }

  @Override
  public MessageResponseDTO get(Long chatId, Long messageId, String clientEmail) {
    checkIsMember(chatId, clientEmail);
    return chatMessageMapper.toDTO(messageService.get(chatId, messageId));
  }

  @Override
  public List<MessageResponseDTO> getAnswers(Long chatId, Long messageId, String clientEmail) {
    checkIsMember(chatId, clientEmail);
    return messageService.getAnswers(chatId, messageId).stream()
        .map(chatMessageMapper::toDTO)
        .toList();
  }

  @Override
  public List<MessageResponseDTO> getPinned(Long chatId, String clientEmail) {
    checkIsMember(chatId, clientEmail);
    return messageService.getPinned(chatId).stream()
        .map(chatMessageMapper::toDTO)
        .toList();
  }

  @Override
  public MessageResponseDTO pin(Long chatId, Long messageId, String clientEmail) {
    checkIsAdminOrModerator(chatId, clientEmail);
    return chatMessageMapper.toDTO(messageService.pin(chatId, messageId));
  }

  @Override
  public MessageResponseDTO unpin(Long chatId, Long messageId, String clientEmail) {
    checkIsAdminOrModerator(chatId, clientEmail);
    return chatMessageMapper.toDTO(messageService.unpin(chatId, messageId));
  }

  @Override
  public void delete(Long chatId, Long messageId, String clientEmail) {
    Message message = messageService.get(chatId, messageId);
    // Удалить сообщение может либо его автор, либо админ/модератор чата
    if (!message.getAuthor().getEmail().equals(clientEmail)) {
      checkIsAdminOrModerator(chatId, clientEmail);
    }
    messageService.delete(message);
  }

  private void checkIsMember(Long chatId, String email) {
    if(chatMemberService.isChatMember(chatId, email)) {
      throw new MessageException("Недостаточно прав для выполнения этой операции");
    }
  }

  private void checkIsAdminOrModerator(Long chatId, String email) {
    ChatMember member = chatMemberService.getMember(chatId, email);
    if(member.getRole().equals(MemberRole.MEMBER)) {
      throw new ChatMemberException("Недостаточно прав для выполнения этой операции");
    }
  }
}
