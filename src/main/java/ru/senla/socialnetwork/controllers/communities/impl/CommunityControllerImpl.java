package ru.senla.socialnetwork.controllers.communities.impl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityController;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityMapper;
import ru.senla.socialnetwork.facade.communities.CommunityFacade;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities")
public class CommunityControllerImpl implements CommunityController {
  private final CommunityFacade communityFacade;

  @Override
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or #dto.owner() == authentication.name")
  public ResponseEntity<CommunityDTO> create(@Valid @RequestBody CreateCommunityDTO dto) {
    CommunityDTO created = communityFacade.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Override
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or #communityServiceImpl.get(id).owner() == authentication.name")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    communityFacade.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<CommunityDTO> get(@PathVariable Long id) {
    CommunityDTO community = communityFacade.get(id);
    return ResponseEntity.ok(community);
  }

  @Override
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or " +
      "#communityServiceImpl.get(changeCommunityDTO.id()).owner() == authentication.name")
  public ResponseEntity<CommunityDTO> change(@Valid @RequestBody
                                             ChangeCommunityDTO changeCommunityDTO) {
    return ResponseEntity.ok(communityFacade.change(changeCommunityDTO));
  }
}
