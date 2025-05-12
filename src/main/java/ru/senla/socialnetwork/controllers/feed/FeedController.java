package ru.senla.socialnetwork.controllers.feed;

import java.util.List;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.PostDTO;

public interface FeedController {
  List<PostDTO> newsFeed(Authentication auth);
}
