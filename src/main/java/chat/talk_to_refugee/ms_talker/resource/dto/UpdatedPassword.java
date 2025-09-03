package chat.talk_to_refugee.ms_talker.resource.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatedPassword(@NotBlank String currentPassword, @NotBlank String newPassword) {
}
