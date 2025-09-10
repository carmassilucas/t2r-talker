package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.usecase.facade.ToggleActiveProfileFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ToggleActiveProfileUseCase {

    private static final Logger log = LoggerFactory.getLogger(ToggleActiveProfileUseCase.class);

    private final ToggleActiveProfileFacade dependencies;

    public ToggleActiveProfileUseCase(ToggleActiveProfileFacade dependencies) {
        this.dependencies = dependencies;
    }

    public void execute(UUID id, Boolean activate) {
        log.info("{} talker profile", activate ? "Activating" : "Disabling");

        var talker = this.dependencies.repository().findById(id)
                .orElseThrow(TalkerNotFoundException::new);

        if (!talker.getActive().equals(activate)) {
            talker.setActive(activate);
            this.dependencies.repository().save(talker);

            log.info("Talker status update done");
            return;
        }

        log.warn("Talker profile already in this state, canceling operation");
    }
}
