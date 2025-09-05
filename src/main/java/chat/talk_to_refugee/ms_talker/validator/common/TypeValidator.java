package chat.talk_to_refugee.ms_talker.validator.common;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Component
public class TypeValidator {

    public Optional<InvalidParam> validate(String type) {
        if (!StringUtils.hasText(type)) {
            return Optional.empty();
        }

        var typeExists = Arrays.stream(TalkerType.Values.values())
                .anyMatch(t -> type.equalsIgnoreCase(t.getDescription()));

        if (!typeExists) {
            return Optional.of(new InvalidParam("type", "type not found"));
        }
        return Optional.empty();
    }
}
