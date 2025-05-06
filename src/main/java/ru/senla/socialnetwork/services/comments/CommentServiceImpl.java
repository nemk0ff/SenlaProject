package ru.senla.socialnetwork.services.comments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.comments.CommentDao;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
  private final CommentDao commentDao;


}
