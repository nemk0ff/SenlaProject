package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;
import ru.senla.socialnetwork.model.users.WallPost;

@Mapper
public interface WallPostMapper {
  WallPostMapper INSTANCE = Mappers.getMapper(WallPostMapper.class);

  WallPostResponseDTO toDTO(WallPost wallPost);

  List<WallPostResponseDTO> toListDTO(List<WallPost> wallPosts);
}
