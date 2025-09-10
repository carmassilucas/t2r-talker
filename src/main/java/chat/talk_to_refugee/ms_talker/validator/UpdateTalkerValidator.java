package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import chat.talk_to_refugee.ms_talker.validator.common.LocationValidator;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import chat.talk_to_refugee.ms_talker.validator.common.UnderageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateTalkerValidator {

    private static final Logger log = LoggerFactory.getLogger(UpdateTalkerValidator.class);

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
        this.typeValidator.validate(List.of((requestBody.getType()))).ifPresent(invalidParams::add);
        this.locationValidator.validate(
                requestBody.getCurrentlyState(), requestBody.getCurrentlyCity()
        ).ifPresent(invalidParams::add);

        if (!invalidParams.isEmpty()) {
            log.warn("Your request parameters didn't validate");
            throw new InvalidDataException(invalidParams);
        }
    }
}
