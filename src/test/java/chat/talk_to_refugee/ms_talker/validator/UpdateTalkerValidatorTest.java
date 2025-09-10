package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import chat.talk_to_refugee.ms_talker.validator.common.LocationValidator;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import chat.talk_to_refugee.ms_talker.validator.common.UnderageValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTalkerValidatorTest {

    @InjectMocks
    private UpdateTalkerValidator validator;

    @Mock private UnderageValidator underageValidator;
    @Mock private TypeValidator typeValidator;
    @Mock private LocationValidator locationValidator;

    @Test
    @DisplayName("Deve lançar exceção quando algum validator retornar parâmetro inválido")
    void should_throw_exception_when_some_validator_returns_invalid_parameter() {
        var requestBody = mock(UpdateTalker.class);
        var invalidParam = mock(InvalidParam.class);
        var now = LocalDate.now().toString();

        when(requestBody.getBirthDate()).thenReturn(now);
        when(this.underageValidator.validate(now)).thenReturn(Optional.of(invalidParam));

        when(requestBody.getType()).thenReturn("collaborator");
        when(this.typeValidator.validate(List.of("collaborator"))).thenReturn(Optional.of(invalidParam));

        assertThrows(InvalidDataException.class, () -> this.validator.validate(requestBody));
    }
}