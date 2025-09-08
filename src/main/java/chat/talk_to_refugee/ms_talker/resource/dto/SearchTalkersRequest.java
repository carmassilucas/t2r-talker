package chat.talk_to_refugee.ms_talker.resource.dto;

import java.util.List;

public record SearchTalkersRequest(String fullName,
                                   String currentlyState,
                                   String currentlyCity,
                                   List<String> type,
                                   Integer page,
                                   Integer size) {
}
