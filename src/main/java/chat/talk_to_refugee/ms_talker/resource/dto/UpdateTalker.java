package chat.talk_to_refugee.ms_talker.resource.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateTalker(String fullName,
                           @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String birthDate,
                           String aboutMe,
                           String currentlyCity,
                           String currentlyState,
                           String type) {
}
