package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.model.communities.Community;

@Mapper
public interface CommunityMapper {
  CommunityMapper INSTANCE = Mappers.getMapper(CommunityMapper.class);

  @Mapping(target = "owner", source = "owner.email")
  CommunityDTO toDTO(Community community);

  List<CommunityDTO> toListDTO(List<Community> communities);

}
