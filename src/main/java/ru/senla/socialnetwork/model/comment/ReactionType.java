package ru.senla.socialnetwork.model.comment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Типы реакций")
public enum ReactionType {
  @Schema(description = "Лайк")
  LIKE,

  @Schema(description = "Дизлайк")
  DISLIKE
}