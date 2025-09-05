package chat.talk_to_refugee.ms_talker.exception;

import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;

public class UpdateTalkerDataException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(UpdateTalkerDataException.class);

    private final List<InvalidParam> invalidParams;

    public UpdateTalkerDataException(List<InvalidParam> invalidParams) {
        this.invalidParams = invalidParams;
    }

    public List<InvalidParam> getInvalidParams() {
        return invalidParams;
    }

    public ProblemDetail toProblemDetail() {
        log.warn("Your request parameters didn't validate");

        var problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Your Request Parameters Didn't Validate");
        problem.setProperty("invalid-params", invalidParams);

        return problem;
    }
}
