package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersRequest;
import chat.talk_to_refugee.ms_talker.validator.common.LocationValidator;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SearchTalkersByFilterValidator {

    private static final Logger log = LoggerFactory.getLogger(SearchTalkersByFilterValidator.class);

    private final LocationValidator locationValidator;
    private final TypeValidator typeValidator;

    public SearchTalkersByFilterValidator(LocationValidator locationValidator, TypeValidator typeValidator) {
        this.locationValidator = locationValidator;
        this.typeValidator = typeValidator;
    }

    public void validate(SearchTalkersRequest requestParams) {
        var invalidParams = new ArrayList<InvalidParam>();

        this.typeValidator.validate(requestParams.type()).ifPresent(invalidParams::add);

        this.locationValidator.validate(
                requestParams.currentlyState(), requestParams.currentlyCity()
        ).ifPresent(invalidParams::add);

        if (!invalidParams.isEmpty()) {
            log.warn("Your request parameters didn't validate");
            throw new InvalidDataException(invalidParams);
        }
    }
}
