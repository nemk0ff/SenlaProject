package ru.senla.socialnetwork.facades.wallpost;

import java.util.List;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;

public interface WallPostFacade {
  List<WallPostResponseDTO> getByUser(String email, String clientEmail);

  WallPostResponseDTO getById(Long postId, String clientEmail);

  WallPostResponseDTO create(WallPostRequestDTO dto, String clientEmail);

  void delete(Long postId, String clientEmail);

  WallPostResponseDTO update(Long postId, WallPostRequestDTO dto, String clientEmail);
}
