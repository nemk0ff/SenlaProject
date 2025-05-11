package ru.senla.socialnetwork.facades.feed;

import java.util.List;
import ru.senla.socialnetwork.dto.PostDTO;

public interface FeedFacade {
  List<PostDTO> getNews(String clientEmail);

}
