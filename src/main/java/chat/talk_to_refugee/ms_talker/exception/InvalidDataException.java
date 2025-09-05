package chat.talk_to_refugee.ms_talker.exception;

import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;

import java.util.List;

public class InvalidDataException extends RuntimeException {

    private final List<InvalidParam> invalidParams;

    public InvalidDataException(List<InvalidParam> invalidParams) {
        this.invalidParams = invalidParams;
    }

    public List<InvalidParam> getInvalidParams() {
        return invalidParams;
    }
}
