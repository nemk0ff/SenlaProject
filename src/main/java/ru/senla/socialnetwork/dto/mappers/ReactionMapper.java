package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.model.comment.Reaction;

@Mapper
public interface ReactionMapper {
  ReactionMapper INSTANCE = Mappers.getMapper(ReactionMapper.class);

  @Mapping(source = "owner.email", target = "email")
  ReactionDTO toDTO(Reaction reaction);

  List<ReactionDTO> toListDTO(List<Reaction> reactions);
}
