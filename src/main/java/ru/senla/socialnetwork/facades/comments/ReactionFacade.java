package ru.senla.socialnetwork.facades.comments;

import java.util.List;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.model.comment.ReactionType;

public interface ReactionFacade {

  List<ReactionDTO> getAll();

  List<ReactionDTO> getByComment(Long commentId, String clientEmail);

  ReactionDTO getById(Long reactionId, String clientEmail);

  ReactionDTO setReaction(Long commentId, ReactionType reactionType, String email);

  void removeReaction(Long id, String email);
}
