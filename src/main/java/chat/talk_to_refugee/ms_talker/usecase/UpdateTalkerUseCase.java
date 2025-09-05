package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.mapper.UpdateTalkerMapper;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import chat.talk_to_refugee.ms_talker.validator.UpdateTalkerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateTalkerUseCase.class);

    private final TalkerRepository repository;
    private final UpdateTalkerValidator validator;
    private final UpdateTalkerMapper mapper;

    public UpdateTalkerUseCase(TalkerRepository repository,
                               UpdateTalkerValidator validator,
                               UpdateTalkerMapper mapper) {
        this.repository = repository;
        this.validator = validator;
        this.mapper = mapper;
    }

    public void execute(UUID id, UpdateTalker requestBody) {
        log.info("Updating talker profile information");

        var talker = this.repository.findById(id)
                .orElseThrow(TalkerNotFoundException::new);

        this.validator.validate(requestBody);

        var updated = this.mapper.updateTalker(requestBody, talker);

        if (StringUtils.hasText(requestBody.getCurrentlyState()) &&
                !StringUtils.hasText(requestBody.getCurrentlyCity())) {
            updated.setCurrentlyCity(null);
        }

        this.repository.save(updated);

        log.info("Talker profile information updated successfully");
    }
}
