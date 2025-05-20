package ru.senla.socialnetwork.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.PostDTO;

@Schema(description = "DTO ответа с информацией о посте")
public record WallPostResponseDTO(
    @Schema(description = "ID поста", example = "123")
    Long id,

    @Schema(description = "Email владельца стены", example = "example@senla.ru")
    String wallOwnerEmail,

    @Schema(description = "Настроение автора", example = "happy")
    String mood,

    @Schema(description = "Текст поста", example = "Сегодня был прекрасный день!")
    String body,

    @Schema(description = "Местоположение", example = "Москва, Россия")
    String location,

    @Schema(description = "Дата и время создания поста", example = "2023-05-20T14:30:00+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt
) implements PostDTO {
}