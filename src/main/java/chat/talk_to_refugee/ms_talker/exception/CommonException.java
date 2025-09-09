package chat.talk_to_refugee.ms_talker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CommonException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(CommonException.class);

    public ProblemDetail toProblemDetail() {
        log.warn("Internal server error");

        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");

        return problem;
    }
}
