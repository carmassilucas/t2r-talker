package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.UpdateTalkerDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UpdateTalkerValidator {

    public void validate(UpdateTalker requestBody) {

        var invalidParams = new ArrayList<InvalidParam>();

        validateBirthDate(requestBody.getBirthDate(), invalidParams);
        validateType(requestBody.getType(), invalidParams);
        validateLocation(requestBody.getCurrentlyState(), requestBody.getCurrentlyCity(), invalidParams);

        if (!invalidParams.isEmpty()) {
            throw new UpdateTalkerDataException(invalidParams);
        }
    }

    private void validateBirthDate(String birthDate, List<InvalidParam> invalidParams) {
        if (birthDate == null || birthDate.isBlank()) {
            return;
        }

        var date = LocalDate.parse(birthDate);
        if (date.isAfter(LocalDate.now().minusYears(18))) {
            invalidParams.add(new InvalidParam("birthDate", "user must be at least 18 years old"));
        }
    }

    private void validateType(String type, List<InvalidParam> invalidParams) {
        if (type == null || type.isBlank()) {
            return;
        }

        var typeExists = Arrays.stream(TalkerType.Values.values())
                .anyMatch(t -> t.getDescription().equals(type));

        if (!typeExists) {
            invalidParams.add(new InvalidParam("type", "type entered is invalid"));
        }
    }

    private void validateLocation(String state, String city, List<InvalidParam> invalidParams) {
        if ((city != null && !city.isBlank()) && (state == null || state.isBlank())) {
            invalidParams.add(new InvalidParam("city", "city reported without corresponding state"));
        }
    }
}
