package chat.talk_to_refugee.ms_talker.resource.dto;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;

import java.time.LocalDate;
import java.util.UUID;

public record TalkerProfile(UUID id,
                            String fullName,
                            String profilePhoto,
                            String aboutMe,
                            LocalDate birthDate,
                            String currentlyCity,
                            String currentlyState,
                            String email,
                            TalkerType type) {
}
