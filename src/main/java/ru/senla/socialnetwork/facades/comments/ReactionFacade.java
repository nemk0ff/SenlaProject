package ru.senla.socialnetwork.facades.comments;

import java.util.List;
import ru.senla.socialnetwork.dto.comments.CreateReactionDTO;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;

public interface ReactionFacade {

  List<ReactionDTO> getAll();

  ReactionDTO get(Long reactionId);

  ReactionDTO setReaction(Long id, CreateReactionDTO request);

  void removeReaction(Long id, String email);

  List<ReactionDTO> getByComment(Long commentId);
}
