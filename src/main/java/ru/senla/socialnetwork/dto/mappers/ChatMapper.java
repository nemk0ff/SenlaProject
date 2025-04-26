package ru.senla.socialnetwork.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.model.chats.Chat;

@Mapper(componentModel = "spring", uses = ChatMemberMapper.class)
public interface ChatMapper {
  @Mapping(target = "members", source = "members")
  ChatDTO chatToChatDTO(Chat chat);
}
