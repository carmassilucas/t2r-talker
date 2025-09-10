package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.resource.dto.TalkerProfile;
import chat.talk_to_refugee.ms_talker.usecase.facade.TalkerProfileFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TalkerProfileUseCase {

    private static final Logger log = LoggerFactory.getLogger(TalkerProfileUseCase.class);

    private final TalkerProfileFacade dependencies;

    public TalkerProfileUseCase(TalkerProfileFacade dependencies) {
        this.dependencies = dependencies;
    }

    public TalkerProfile execute(UUID id) {
        log.info("Recovering talker profile");

        var talker = this.dependencies.repository().findById(id)
                .orElseThrow(TalkerNotFoundException::new);

        log.info("Talker found, mapping and returning profile");

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
