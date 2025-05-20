package ru.senla.socialnetwork.dto.communitites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "DTO для блокировки участника сообщества")
public record BanCommunityMemberDTO(
    @Schema(description = "Email участника", example = "example@senla.ru", requiredMode =
        Schema.RequiredMode.REQUIRED)
    @Email String email,

    @Schema(description = "Причина блокировки", example = "Нарушение правил сообщества")
    String reason
) {
}