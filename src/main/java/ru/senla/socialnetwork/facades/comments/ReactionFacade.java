package ru.senla.socialnetwork.facades.comments;

import java.util.List;
import ru.senla.socialnetwork.dto.comments.CreateReactionDTO;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;

public interface ReactionFacade {

  List<ReactionDTO> getAll();

  List<ReactionDTO> getByComment(Long commentId, String clientEmail);

  ReactionDTO getById(Long reactionId, String clientEmail);

  ReactionDTO setReaction(CreateReactionDTO request, String email);

  void removeReaction(Long id, String email);
}
