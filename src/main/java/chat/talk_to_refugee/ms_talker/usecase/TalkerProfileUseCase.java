package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.TalkerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TalkerProfileUseCase {

    private static final Logger log = LoggerFactory.getLogger(TalkerProfileUseCase.class);

    private final TalkerRepository repository;

    public TalkerProfileUseCase(TalkerRepository repository) {
        this.repository = repository;
    }

    public TalkerProfile execute(UUID id) {
        log.debug("Recuperando perfil do talker {}", id);
        var talker = this.repository.findById(id).orElseThrow(() -> {
            log.debug("Talker {} não encontrado", id);
            return new TalkerNotFoundException();
        });

        log.debug("Talker {} encontrado, mapeando e retornando perfil", id);
        return new TalkerProfile(
                talker.getId(),
                talker.getFullName(),
                talker.getProfilePhoto(),
                talker.getAboutMe(),
                talker.getBirthDate(),
                talker.getCurrentlyCity(),
                talker.getCurrentlyState(),
                talker.getEmail(),
                talker.getType()
        );
    }
}
