package ru.senla.socialnetwork.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.senla.socialnetwork.dto.chats.ChatMessageDTO;
import ru.senla.socialnetwork.model.content.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
  @Mapping(target = "authorEmail", source = "author.user.email")
  @Mapping(target = "replyToId", source = "replyTo.id")
  ChatMessageDTO toDTO(ChatMessage message);
}
