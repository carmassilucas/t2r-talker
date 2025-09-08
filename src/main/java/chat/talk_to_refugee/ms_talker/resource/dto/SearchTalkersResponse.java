package chat.talk_to_refugee.ms_talker.resource.dto;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;

import java.util.UUID;

public record SearchTalkersResponse(UUID id,
                                    String fullName,
                                    String profilePhoto,
                                    String aboutMe,
                                    Integer age,
                                    String currentlyState,
                                    String currentlyCity,
                                    TalkerType type) {
}
