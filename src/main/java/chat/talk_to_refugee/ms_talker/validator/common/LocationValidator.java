package chat.talk_to_refugee.ms_talker.validator.common;

import chat.talk_to_refugee.ms_talker.client.LocationAPI;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;

@Component
public class LocationValidator {
    
    private final LocationAPI locationAPI;

    public LocationValidator(LocationAPI locationAPI) {
        this.locationAPI = locationAPI;
    }

    public Optional<InvalidParam> validate(String state, String city) {
        var isFilledCity = StringUtils.hasText(city);
        var isFilledState = StringUtils.hasText(state);

        if (!isFilledCity && !isFilledState) {
            return Optional.empty();
        }

        if (isFilledCity && !isFilledState) {
            return Optional.of(new InvalidParam("city", "city reported without corresponding state"));
        }

        var locations = Optional.ofNullable(locationAPI.findLocationsByState(state))
                .orElse(Collections.emptyList());

        if (locations.isEmpty()) {
            return Optional.of(new InvalidParam("state", "state not found"));
        }

        if (isFilledCity) {
            var cityFound = locations.stream()
                    .anyMatch(location -> location.getCurrentlyCity().equals(city));

            if (!cityFound) {
                return Optional.of(new InvalidParam("city", "city not found"));
            }
        }

        return Optional.empty();
    }
}
