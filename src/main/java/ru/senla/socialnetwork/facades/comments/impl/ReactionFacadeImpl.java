package ru.senla.socialnetwork.facades.comments.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.comments.CreateReactionDTO;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.facades.comments.ReactionFacade;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReactionFacadeImpl implements ReactionFacade {

  @Override
  public List<ReactionDTO> getAll() {
    return List.of();
  }

  @Override
  public ReactionDTO get(Long reactionId) {
    return null;
  }

  @Override
  public ReactionDTO setReaction(Long id, CreateReactionDTO request) {
    return null;
  }

  @Override
  public void removeReaction(Long id, String email) {

  }

  @Override
  public List<ReactionDTO> getByComment(Long commentId) {
    return List.of();
  }
}
