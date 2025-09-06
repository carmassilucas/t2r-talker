package chat.talk_to_refugee.ms_talker.validator.common;

import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnderageValidatorTest {

    @InjectMocks
    private UnderageValidator validator;

    @Test
    @DisplayName("Deve retornar vazio quando parâmetro válido")
    void should_returns_empty_when_valid_parameter() {
        var invalidParam = this.validator.validate("2000-01-01");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar parâmetro inválido quando menor de idade")
    void should_returns_invalid_parameter_when_underage() {
        var invalidParam = this.validator.validate(LocalDate.now().toString());
        var expectedInvalidParam = new InvalidParam("birthDate", "user must be at least 18 years old");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isPresent());
        assertEquals(expectedInvalidParam, invalidParam.get());
    }

    @Test
    @DisplayName("Deve retornar vazio quando parâmetro vazio")
    void should_returns_empty_when_empty_parameter() {
        var invalidParam = this.validator.validate("");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isEmpty());
    }
}