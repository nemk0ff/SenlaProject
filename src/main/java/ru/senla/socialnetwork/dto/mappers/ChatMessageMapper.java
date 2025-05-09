package ru.senla.socialnetwork.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.senla.socialnetwork.dto.chats.MessageDTO;
import ru.senla.socialnetwork.model.chats.Message;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
  @Mapping(target = "authorEmail", source = "author.email")
  @Mapping(target = "replyToId", source = "replyTo.id")
  MessageDTO toDTO(Message message);
}
