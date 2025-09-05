package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.UpdateTalkerDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UpdateTalkerValidatorTest {

    @InjectMocks
    private UpdateTalkerValidator validator;

    @Test
    @DisplayName("Deve lançar exceção quando valores da requisição inválidos")
    void should_throw_exception_when_invalid_request_values() {
        var requestBody = new UpdateTalker();
        requestBody.setBirthDate("2020-01-01");
        requestBody.setCurrentlyCity("City");
        requestBody.setType("invalid-type");

        var expectedInvalidParams = new HashSet<InvalidParam>();
        expectedInvalidParams.add(new InvalidParam("birthDate", "user must be at least 18 years old"));
        expectedInvalidParams.add(new InvalidParam("type", "type entered is invalid"));
        expectedInvalidParams.add(new InvalidParam("city", "city reported without corresponding state"));

        var exception = assertThrows(UpdateTalkerDataException.class, () -> this.validator.validate(requestBody));
        var invalidParams = new HashSet<>(exception.getInvalidParams());

        assertNotNull(exception);
        assertEquals(3, invalidParams.size());
        assertEquals(expectedInvalidParams, invalidParams);
    }

    @Test
    @DisplayName("Deve não lançar exceção quando dados da requisição válidos")
    void should_not_throw_exception_when_valid_request_values() {
        var requestBody = new UpdateTalker();
        requestBody.setBirthDate("2000-01-01");
        requestBody.setCurrentlyState("State");
        requestBody.setCurrentlyCity("City");
        requestBody.setType("collaborator");

        assertDoesNotThrow(() -> this.validator.validate(requestBody));
    }

    @Test
    @DisplayName("Deve não validar valor quando nulo")
    void should_not_validate_value_when_null() {
        var requestBody = new UpdateTalker();

        assertDoesNotThrow(() -> this.validator.validate(requestBody));
    }

    @Test
    @DisplayName("Deve não validar valor quando em branco")
    void should_not_validate_value_when_blank() {
        var requestBody = new UpdateTalker();
        requestBody.setBirthDate(" ");
        requestBody.setType(" ");

        assertDoesNotThrow(() -> this.validator.validate(requestBody));
    }
}