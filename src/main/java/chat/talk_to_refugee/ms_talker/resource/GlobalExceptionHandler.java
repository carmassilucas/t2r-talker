package chat.talk_to_refugee.ms_talker.resource;

import chat.talk_to_refugee.ms_talker.exception.CommonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ProblemDetail handleCommonException(CommonException ex) {
        return ex.toProblemDetail();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail badCredentialsException(BadCredentialsException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("your request parameters didn't validate");

        var invalidParams = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new InvalidParam(error.getField(), error.getDefaultMessage()));

        problem.setProperty("invalid-params", invalidParams);

        return problem;
    }

    private record InvalidParam(String name, String reason) {
    }
}
