package chat.talk_to_refugee.ms_talker.validator.common;

import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class UnderageValidator {

    public Optional<InvalidParam> validate(String birthDate) {
        if (!StringUtils.hasText(birthDate)) {
            return Optional.empty();
        }

        var date = LocalDate.parse(birthDate);
        if (date.isAfter(LocalDate.now().minusYears(18))) {
            return Optional.of(new InvalidParam("birthDate", "user must be at least 18 years old"));
        }

        return Optional.empty();
    }
}
