package chat.talk_to_refugee.ms_talker.validator.common;

import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TypeValidatorTest {

    @InjectMocks
    private TypeValidator validator;

    @ParameterizedTest
    @ValueSource(strings = { "collaborator", "immigrant", "refugee" })
    @DisplayName("Deve retornar vazio quando parâmetro válido")
    void should_returns_empty_when_valid_parameter(String type) {
        var invalidParam = this.validator.validate(type);

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar vazio quando parâmetro vazio")
    void should_returns_empty_when_empty_parameter() {
        var invalidParam = this.validator.validate("");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar parâmetro inválido quando tipo inválido")
    void should_returns_invalid_parameter_when_invalid_type() {
        var invalidParam = this.validator.validate("invalid-type");
        var expectedInvalidParam = new InvalidParam("type", "type not found");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isPresent());
        assertEquals(expectedInvalidParam, invalidParam.get());
    }

}