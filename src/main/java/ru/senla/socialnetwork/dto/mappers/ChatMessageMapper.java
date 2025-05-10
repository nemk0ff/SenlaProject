package ru.senla.socialnetwork.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.senla.socialnetwork.dto.chats.MessageResponseDTO;
import ru.senla.socialnetwork.model.chats.Message;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
  @Mapping(target = "chatId", source = "chat.id")
  @Mapping(target = "authorEmail", source = "author.email")
  @Mapping(target = "replyToId", source = "replyTo.id")
  MessageResponseDTO toDTO(Message message);
}
