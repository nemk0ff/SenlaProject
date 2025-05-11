package ru.senla.socialnetwork.controllers.feed.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.feed.FeedController;
import ru.senla.socialnetwork.dto.PostDTO;
import ru.senla.socialnetwork.facades.feed.FeedFacade;


// TODO: (возможно) поиск постов по mood, location, key words
// TODO: (возможно) рекомендованные публикации (хз как, хз стоит ли)


@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/feed")
public class FeedControllerImpl implements FeedController {
  private final FeedFacade feedFacade;

  @Override
  @GetMapping()
  public List<PostDTO> newsFeed(Authentication auth) {
    log.info("Получение ленты новостей пользователем {}", auth.getName());
    return feedFacade.getNews(auth.getName());
  }
}
