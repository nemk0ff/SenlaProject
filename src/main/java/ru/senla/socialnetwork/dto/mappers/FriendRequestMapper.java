package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;

@Mapper
public interface FriendRequestMapper {
  FriendRequestMapper INSTANCE = Mappers.getMapper(FriendRequestMapper.class);

  @Mapping(target = "senderEmail", source = "sender.email")
  @Mapping(target = "recipientEmail", source = "recipient.email")
  FriendRequestDTO toDTO(FriendRequest friendRequest);

  List<FriendRequestDTO> toListDTO(List<FriendRequest> friendRequests);

}