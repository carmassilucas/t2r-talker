package chat.talk_to_refugee.ms_talker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UnderageException extends CommonException {

    @Override
    public ProblemDetail toProblemDetail() {
        var problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("minors are not allowed to register");

        return problem;
    }
}
