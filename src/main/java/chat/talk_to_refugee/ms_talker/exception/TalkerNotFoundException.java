package chat.talk_to_refugee.ms_talker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TalkerNotFoundException extends CommonException {

    private static final Logger log = LoggerFactory.getLogger(TalkerNotFoundException.class);

    @Override
    public ProblemDetail toProblemDetail() {
        log.warn("Talker not found");

        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Talker Not Found");

        return problem;
    }
}
