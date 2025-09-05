package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import chat.talk_to_refugee.ms_talker.validator.common.LocationValidator;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import chat.talk_to_refugee.ms_talker.validator.common.UnderageValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UpdateTalkerValidator {

    private final UnderageValidator underageValidator;
    private final TypeValidator typeValidator;
    private final LocationValidator locationValidator;

    public UpdateTalkerValidator(UnderageValidator underageValidator,
                                 TypeValidator typeValidator,
                                 LocationValidator locationValidator) {
        this.underageValidator = underageValidator;
        this.typeValidator = typeValidator;
        this.locationValidator = locationValidator;
    }

    public void validate(UpdateTalker requestBody) {

        var invalidParams = new ArrayList<InvalidParam>();

        this.underageValidator.validate(requestBody.getBirthDate()).ifPresent(invalidParams::add);
        this.typeValidator.validate(requestBody.getType()).ifPresent(invalidParams::add);
        this.locationValidator.validate(
                requestBody.getCurrentlyState(), requestBody.getCurrentlyCity()
        ).ifPresent(invalidParams::add);

        if (!invalidParams.isEmpty()) {
            throw new InvalidDataException(invalidParams);
        }
    }
}
