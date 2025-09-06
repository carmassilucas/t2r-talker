package chat.talk_to_refugee.ms_talker.validator.common;

import chat.talk_to_refugee.ms_talker.client.LocationAPI;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.LocationAPIResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationValidatorTest {

    @InjectMocks
    private LocationValidator validator;

    @Mock
    private LocationAPI locationAPI;

    @Test
    @DisplayName("Deve retornar vazio quando parâmetros válidos")
    void should_returns_empty_when_valid_parameters() {
        var response = new LocationAPIResponse();

        var uf = new LocationAPIResponse.Microregion.Mesoregion.UF();
        uf.setCurrentlyState("valid state");

        var mesoregion = new LocationAPIResponse.Microregion.Mesoregion();
        mesoregion.setUf(uf);

        var microregion = new LocationAPIResponse.Microregion();
        microregion.setMesoregion(mesoregion);

        response.setCurrentlyCity("valid city");
        response.setMicroregion(microregion);

        when(this.locationAPI.findLocationsByState("valid state")).thenReturn(List.of(response));

        var invalidParam = this.validator.validate("valid state", "valid city");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar vazio quando parâmetros vazios")
    void should_returns_empty_when_empty_parameters() {
        var invalidParam = this.validator.validate("", "");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar parâmetro inválido quando cidade preenchida e estado não")
    void should_returns_invalid_parameter_when_city_filled_and_state_not() {
        var invalidParam = this.validator.validate("", "valid city");
        var expectedInvalidParam = new InvalidParam("city", "city reported without corresponding state");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isPresent());
        assertEquals(expectedInvalidParam, invalidParam.get());
    }

    @Test
    @DisplayName("Deve retornar parâmetro inválido quando estado não encontrado")
    void should_returns_invalid_parameter_when_state_not_found() {
        when(this.locationAPI.findLocationsByState("invalid state")).thenReturn(Collections.emptyList());

        var invalidParam = this.validator.validate("invalid state", "");
        var expectedInvalidParam = new InvalidParam("state", "state not found");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isPresent());
        assertEquals(expectedInvalidParam, invalidParam.get());
    }

    @Test
    @DisplayName("Deve retornar parâmetro inválido cidade não encontrada")
    void should_returns_invalid_parameter_when_city_not_found() {
        var response = new LocationAPIResponse();

        var uf = new LocationAPIResponse.Microregion.Mesoregion.UF();
        uf.setCurrentlyState("valid state");

        var mesoregion = new LocationAPIResponse.Microregion.Mesoregion();
        mesoregion.setUf(uf);

        var microregion = new LocationAPIResponse.Microregion();
        microregion.setMesoregion(mesoregion);

        response.setCurrentlyCity("city");
        response.setMicroregion(microregion);

        when(this.locationAPI.findLocationsByState("valid state")).thenReturn(List.of(response));

        var invalidParam = this.validator.validate("valid state", "invalid city");
        var expectedInvalidParam = new InvalidParam("city", "city not found");

        assertNotNull(invalidParam);
        assertTrue(invalidParam.isPresent());
        assertEquals(expectedInvalidParam, invalidParam.get());
    }
}