package ru.senla.socialnetwork.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.senla.socialnetwork.dto.chats.ChatMemberDTO;
import ru.senla.socialnetwork.model.chats.ChatMember;

@Mapper(componentModel = "spring")
public interface ChatMemberMapper {

  @Mapping(target = "email", source = "user.email")
  ChatMemberDTO memberToDTO(ChatMember chatMember);


}
