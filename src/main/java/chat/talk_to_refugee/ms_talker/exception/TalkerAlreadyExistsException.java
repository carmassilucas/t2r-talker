package chat.talk_to_refugee.ms_talker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TalkerAlreadyExistsException extends CommonException {

    private static final Logger log = LoggerFactory.getLogger(TalkerAlreadyExistsException.class);

    @Override
    public ProblemDetail toProblemDetail() {
        log.warn("E-mail address already exists");

        var problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("E-mail Address Already Exists");

        return problem;
    }
}
