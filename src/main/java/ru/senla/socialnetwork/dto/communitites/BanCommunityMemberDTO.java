package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.Email;

public record BanCommunityMemberDTO(
    @Email String email,
    String reason
) {
}
