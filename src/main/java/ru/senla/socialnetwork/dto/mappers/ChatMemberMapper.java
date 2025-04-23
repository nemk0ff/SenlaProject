package ru.senla.socialnetwork.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.chats.ChatMember;

@Mapper
public interface ChatMemberMapper {
  ChatMemberMapper INSTANCE = Mappers.getMapper(ChatMemberMapper.class);

  @Mapping(target = "email", source = "user.email")
  ChatMemberDTO memberToDTO(ChatMember chatMember);
}
