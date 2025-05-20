package ru.senla.socialnetwork.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ при удалении объекта")
public record DeleteResponseDTO(
    @Schema(description = "Сообщение об успешном удалении", example = "Чат успешно удалён")
    String message,

    @Schema(description = "Дополнительные данные", example = "{\"chatId\": 1}")
    Object data
) {
}