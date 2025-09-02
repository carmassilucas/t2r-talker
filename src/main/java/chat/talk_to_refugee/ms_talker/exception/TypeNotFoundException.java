package chat.talk_to_refugee.ms_talker.exception;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Arrays;

public class TypeNotFoundException extends CommonException {

    @Override
    public ProblemDetail toProblemDetail() {
        var problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("unexpected request body format");
        problem.setDetail("type must be between: " + Arrays.toString(TalkerType.Values.values()));

        return problem;

    }
}
