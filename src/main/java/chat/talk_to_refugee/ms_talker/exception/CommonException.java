package chat.talk_to_refugee.ms_talker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CommonException extends RuntimeException {

    public ProblemDetail toProblemDetail() {
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("internal server error");

        return problem;
    }
}
