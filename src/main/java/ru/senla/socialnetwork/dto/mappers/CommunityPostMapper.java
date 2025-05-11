package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.model.communities.CommunityPost;

@Mapper
public interface CommunityPostMapper {
  CommunityPostMapper INSTANCE = Mappers.getMapper(CommunityPostMapper.class);

  @Mapping(target = "communityId", source = "community.id")
  @Mapping(target = "authorEmail", source = "author.user.email")
  CommunityPostDTO toDTO(CommunityPost post);

  List<CommunityPostDTO> toListDTO(List<CommunityPost> posts);
}
