package ru.senla.socialnetwork.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.model.chats.Chat;

@Mapper
public interface ChatMapper {
  ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

  ChatDTO chatToChatDTO(Chat chat);
}
