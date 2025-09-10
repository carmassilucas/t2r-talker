package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import chat.talk_to_refugee.ms_talker.usecase.facade.CreateTalkerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CreateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateTalkerUseCase.class);

    private final CreateTalkerFacade dependencies;

    public CreateTalkerUseCase(CreateTalkerFacade dependencies) {
        this.dependencies = dependencies;
    }

    @Transactional
    public void execute(CreateTalker requestBody) {
        log.info("Registering a new talker");

        this.dependencies.validator().validate(requestBody);

        this.dependencies.repository().findByEmail(requestBody.email()).ifPresent(talker -> {
            throw new TalkerAlreadyExistsException();
        });

        this.dependencies.repository().save(new Talker(
                requestBody.fullName(),
                LocalDate.parse(requestBody.birthDate()),
                requestBody.email(),
                this.dependencies.passwordEncoder().encode(requestBody.password()),
                TalkerType.Values.valueOf(requestBody.type().toUpperCase()).get()
        ));

        log.info("Registration completed successfully");
    }
}
