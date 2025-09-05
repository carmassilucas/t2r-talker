package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import chat.talk_to_refugee.ms_talker.validator.common.UnderageValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CreateTalkerValidator {

    private final UnderageValidator underageValidator;
    private final TypeValidator typeValidator;

    public CreateTalkerValidator(UnderageValidator underageValidator, TypeValidator typeValidator) {
        this.underageValidator = underageValidator;
        this.typeValidator = typeValidator;
    }

    public void validate(CreateTalker requestBody) {

        var invalidParams = new ArrayList<InvalidParam>();

        this.underageValidator.validate(requestBody.birthDate()).ifPresent(invalidParams::add);
        this.typeValidator.validate(requestBody.type()).ifPresent(invalidParams::add);

        if (!invalidParams.isEmpty()) {
            throw new InvalidDataException(invalidParams);
        }
    }
}
