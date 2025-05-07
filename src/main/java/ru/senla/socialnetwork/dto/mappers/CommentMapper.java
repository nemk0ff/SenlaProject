package ru.senla.socialnetwork.dto.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.model.comment.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  @Mapping(source = "post.id", target = "postId")
  @Mapping(source = "author.id", target = "authorId")
  CommentDTO toDto(Comment comment);

  List<CommentDTO> toListDto(List<Comment> comments);
}
