package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.CityWithoutStateException;
import chat.talk_to_refugee.ms_talker.exception.CommonException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.exception.TypeNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Service
public class UpdateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateTalkerUseCase.class);
    private final TalkerRepository repository;

    public UpdateTalkerUseCase(TalkerRepository repository) {
        this.repository = repository;
    }

    // todo: refatorar para funcionar com map sctruct
    public void execute(UUID id, UpdateTalker requestBody) {
        log.info("Atualizando informações de perfil do talker {}", id);

        var talker = this.repository.findById(id).orElseThrow(() -> {
            log.warn("Talker {} não encontrado", id);
            return new TalkerNotFoundException();
        });

        if (isInvalidLocation(requestBody.currentlyState(), requestBody.currentlyCity())) {
            log.warn("Tentativa de atualização de cidade sem estado, atualização cancelada");
            throw new CityWithoutStateException();
        }

        var contador = 0;
        for (var sourceField : requestBody.getClass().getDeclaredFields()) {
            sourceField.setAccessible(true);

            try {
                var name = sourceField.getName();
                var value = sourceField.get(requestBody);

                if (value == null || String.valueOf(value).isBlank()) {
                    continue;
                }

                var targetField = talker.getClass().getDeclaredField(name);
                targetField.setAccessible(true);

                if (targetField.getType() == LocalDate.class) {
                    targetField.set(talker, LocalDate.parse(requestBody.birthDate()));

                    contador++;
                    continue;
                }

                if (targetField.getType() == TalkerType.class) {
                    var optional = Arrays.stream(TalkerType.Values.values()).filter(
                            t -> t.name().equalsIgnoreCase(requestBody.type())
                    ).findFirst();

                    if (optional.isEmpty()) {
                        log.warn("Tipo {} não existe para talker", requestBody.type());
                        throw new TypeNotFoundException();
                    }

                    var type = optional.get();
                    targetField.set(talker, type.get());

                    contador++;
                    continue;
                }

                targetField.set(talker, String.valueOf(value));
                contador++;
            } catch (IllegalAccessException | NoSuchFieldException | IllegalArgumentException ex) {
                log.error("Erro ao mapear campo '{}' do talker {}: {}", sourceField.getName(), id, ex.getMessage(), ex);
                throw new CommonException();
            }
        }
        this.repository.save(talker);

        log.info("Foram atualizadas {} informações de perfil do talker {} ", contador, id);
    }

    private boolean isInvalidLocation(String currentlyState, String currentlyCity) {
        return (currentlyCity != null && !currentlyCity.isBlank()) &&
                (currentlyState == null || currentlyState.isBlank());
    }
}
