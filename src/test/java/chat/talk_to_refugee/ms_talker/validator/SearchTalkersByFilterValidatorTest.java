package chat.talk_to_refugee.ms_talker.validator;

import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersRequest;
import chat.talk_to_refugee.ms_talker.validator.common.LocationValidator;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchTalkersByFilterValidatorTest {

    @InjectMocks
    private SearchTalkersByFilterValidator validator;

    @Mock private LocationValidator locationValidator;
    @Mock private TypeValidator typeValidator;

    @Test
    @DisplayName("Deve lançar exceção quando algum validator retornar parâmetro inválido")
    void should_throw_exception_when_some_validator_returns_invalid_parameter() {
        var requestParams = mock(SearchTalkersRequest.class);
        var invalidParam = mock(InvalidParam.class);

        when(requestParams.type()).thenReturn(List.of("collaborator"));
        when(this.typeValidator.validate(List.of("collaborator"))).thenReturn(Optional.of(invalidParam));

        assertThrows(InvalidDataException.class, () -> this.validator.validate(requestParams));
    }
}