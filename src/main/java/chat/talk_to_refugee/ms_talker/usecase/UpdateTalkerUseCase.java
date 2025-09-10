package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import chat.talk_to_refugee.ms_talker.usecase.facade.UpdateTalkerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateTalkerUseCase.class);

    private final UpdateTalkerFacade dependencies;

    public UpdateTalkerUseCase(UpdateTalkerFacade dependencies) {
        this.dependencies = dependencies;
    }

    public void execute(UUID id, UpdateTalker requestBody) {
        log.info("Updating talker profile information");

        var talker = this.dependencies.repository().findById(id)
                .orElseThrow(TalkerNotFoundException::new);

        this.dependencies.validator().validate(requestBody);

        var updated = this.dependencies.mapper().updateTalker(requestBody, talker);

        if (StringUtils.hasText(requestBody.getCurrentlyState()) &&
                !StringUtils.hasText(requestBody.getCurrentlyCity())) {
            updated.setCurrentlyCity(null);
        }

        this.dependencies.repository().save(updated);

        log.info("Talker profile information updated successfully");
    }
}
