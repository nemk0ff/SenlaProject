package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.model.communities.CommunityMember;

@Mapper
public interface CommunityMemberMapper {
  CommunityMemberMapper INSTANCE = Mappers.getMapper(CommunityMemberMapper.class);

  @Mapping(target = "community", source = "community.email")
  CommunityMemberDTO toDTO(CommunityMember member);

  List<CommunityMemberDTO> toListDTO(List<CommunityMember> members);
}
