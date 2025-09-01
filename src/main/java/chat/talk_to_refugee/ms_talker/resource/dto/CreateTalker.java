package chat.talk_to_refugee.ms_talker.resource.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateTalker(@NotBlank String fullName,
                           @NotBlank @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String birthDate,
                           @NotBlank @Email String email,
                           @NotBlank String password) {
}
