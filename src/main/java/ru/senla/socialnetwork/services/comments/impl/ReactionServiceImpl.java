package ru.senla.socialnetwork.services.comments.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.comments.ReactionDao;
import ru.senla.socialnetwork.exceptions.comments.ReactionException;
import ru.senla.socialnetwork.model.comment.Reaction;
import ru.senla.socialnetwork.services.comments.ReactionService;

@Slf4j
@Service
@AllArgsConstructor
public class ReactionServiceImpl implements ReactionService {
  private final ReactionDao reactionDao;

  @Override
  public List<Reaction> getAll() {
    return reactionDao.getAll();
  }

  @Override
  public List<Reaction> getAllByComment(Long commentId) {
    return reactionDao.getAllByComment(commentId);
  }

  @Override
  public Reaction get(Long reactionId) {
    return reactionDao.find(reactionId).orElseThrow(
        () -> new ReactionException("Реакция не найдена"));
  }

  @Override
  public Reaction save(Reaction reaction) {
    Optional<Reaction> maybeReaction = reactionDao.getByUserAndComment(
        reaction.getComment().getId(), reaction.getOwner().getId());
    if (maybeReaction.isPresent()) {
      if (!maybeReaction.get().getType().equals(reaction.getType())) {
        Reaction updatedReaction = maybeReaction.get();
        updatedReaction.setType(reaction.getType());
        updatedReaction.setCreatedAt(ZonedDateTime.now());

        return reactionDao.saveOrUpdate(updatedReaction);
      } else {
        return maybeReaction.get();
      }
    }
    return reactionDao.saveOrUpdate(reaction);
  }

  @Override
  public void delete(Reaction reaction) {
    reactionDao.delete(reaction);
  }
}
