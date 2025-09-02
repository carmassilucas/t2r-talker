package chat.talk_to_refugee.ms_talker.resource.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank @Email String email, @NotBlank String password) {
}
