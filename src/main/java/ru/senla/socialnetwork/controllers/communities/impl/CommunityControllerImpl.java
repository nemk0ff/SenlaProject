package ru.senla.socialnetwork.controllers.communities.impl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityController;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.facades.communities.CommunityFacade;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities")
public class CommunityControllerImpl implements CommunityController {
  private final CommunityFacade communityFacade;

  @Override
  @PostMapping
  public ResponseEntity<?> create(
      @Valid @RequestBody CreateCommunityDTO dto,
      Authentication auth) {
    log.info("Пользователь {} создает новое сообщество '{}'", auth.getName(), dto.name());
    CommunityDTO created = communityFacade.create(dto, auth.getName());
    log.info("Создано сообщество id={}, название: '{}', владелец: {}",
        created.id(), created.name(), auth.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(
      @PathVariable Long id,
      Authentication auth) {
    log.info("Пользователь {} удаляет сообщества id={}", auth.getName(), id);
    communityFacade.delete(id, auth.getName());
    log.warn("Сообщество id={} удалено пользователем {}", id, auth.getName());
    return ResponseEntity.ok("Сообщество " + id + " удалено");
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable Long id) {
    log.info("Запрос информации о сообществе id={}", id);
    CommunityDTO community = communityFacade.get(id);
    log.info("Возвращена информация о сообществе id={}, название: '{}'", id, community.name());
    return ResponseEntity.ok(community);
  }

  @Override
  @PutMapping
  public ResponseEntity<?> change(
      @Valid @RequestBody ChangeCommunityDTO changeCommunityDTO,
      Authentication auth) {
    log.info("Пользователь {} изменяет сообщество ID: {}", auth.getName(), changeCommunityDTO.id());
    CommunityDTO updated = communityFacade.change(changeCommunityDTO, auth.getName());
    log.info("Сообщество ID: {} успешно обновлено. Новое название: '{}'", updated.id(), updated.name());
    return ResponseEntity.ok(updated);
  }
}