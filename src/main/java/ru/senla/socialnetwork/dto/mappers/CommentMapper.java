package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.model.comment.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  CommentDTO toDto(Comment comment);

  List<CommentDTO> toListDto(List<Comment> comments);
}
