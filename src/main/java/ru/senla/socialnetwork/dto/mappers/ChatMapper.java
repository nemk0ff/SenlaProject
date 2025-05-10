package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.senla.socialnetwork.dto.chats.ChatDTO;
import ru.senla.socialnetwork.model.chats.Chat;

@Mapper(componentModel = "spring", uses = ChatMemberMapper.class)
public interface ChatMapper {

  @Mapping(target = "members", source = "members")
  ChatDTO toChatDTO(Chat chat);

  List<ChatDTO> toListChatDTO(List<Chat> chats);
}
