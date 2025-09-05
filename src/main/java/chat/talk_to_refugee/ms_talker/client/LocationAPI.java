package chat.talk_to_refugee.ms_talker.client;

import chat.talk_to_refugee.ms_talker.resource.dto.LocationAPIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "location-api", url = "https://servicodados.ibge.gov.br", path = "/api/v1/localidades/estados")
public interface LocationAPI {

    @GetMapping(value = "/{state}/municipios")
    List<LocationAPIResponse> findLocationsByState(@PathVariable("state") String state);

}
