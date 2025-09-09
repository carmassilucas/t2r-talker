package chat.talk_to_refugee.ms_talker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class PasswordNotMatchException extends CommonException {

    private static final Logger log = LoggerFactory.getLogger(PasswordNotMatchException.class);

    @Override
    public ProblemDetail toProblemDetail() {
        log.warn("Current password is invalid");

        var problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Current Password is Invalid");

        return problem;
    }
}
