package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import chat.talk_to_refugee.ms_talker.validator.common.UnderageValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTalkerValidatorTest {

    @InjectMocks
    private CreateTalkerValidator validator;

    @Mock
    private UnderageValidator underageValidator;

    @Mock
    private TypeValidator typeValidator;

    @Test
    @DisplayName("Deve lançar exceção quando algum validator retornar parâmetro inválido")
    void should_throw_exception_when_some_validator_returns_invalid_parameter() {
        var requestBody = mock(CreateTalker.class);
        var invalidParam = mock(InvalidParam.class);
        var now = LocalDate.now().toString();

        when(requestBody.birthDate()).thenReturn(now);
        when(this.underageValidator.validate(now)).thenReturn(Optional.of(invalidParam));

        assertThrows(InvalidDataException.class, () -> this.validator.validate(requestBody));
    }
}