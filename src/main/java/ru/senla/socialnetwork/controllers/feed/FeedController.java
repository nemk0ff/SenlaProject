package ru.senla.socialnetwork.controllers.feed;

import java.util.List;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.PostDTO;
import ru.senla.socialnetwork.model.Post;

public interface FeedController {
  List<PostDTO> getNews(Authentication auth);
}
