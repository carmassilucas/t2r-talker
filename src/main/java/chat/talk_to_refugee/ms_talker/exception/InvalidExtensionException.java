package chat.talk_to_refugee.ms_talker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.stream.Stream;

public class InvalidExtensionException extends CommonException {

    private static final Logger log = LoggerFactory.getLogger(InvalidExtensionException.class);

    private final Stream<String> allowedExtensions;

    public InvalidExtensionException(Stream<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        log.warn("Unexpected file extension");

        var problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Unexpected File Extension");
        problem.setDetail("Allowed extension: " + allowedExtensions.toList());

        return problem;
    }
}
