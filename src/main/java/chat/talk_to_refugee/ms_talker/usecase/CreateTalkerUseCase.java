package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import chat.talk_to_refugee.ms_talker.validator.CreateTalkerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CreateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateTalkerUseCase.class);

    private final CreateTalkerValidator validator;
    private final TalkerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CreateTalkerUseCase(CreateTalkerValidator validator,
                               TalkerRepository repository,
                               PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(CreateTalker requestBody) {
        log.info("Solicitação de cadastro de talker {}", requestBody.email());

        this.validator.validate(requestBody);

        this.repository.findByEmail(requestBody.email()).ifPresent(talker -> {
            log.warn("Endereço de e-mail já cadastrado");
            throw new TalkerAlreadyExistsException();
        });

        var talker = this.repository.save(new Talker(
                requestBody.fullName(),
                LocalDate.parse(requestBody.birthDate()),
                requestBody.email(),
                this.passwordEncoder.encode(requestBody.password()),
                TalkerType.Values.valueOf(requestBody.type().toUpperCase()).get()
        ));
        log.info("Talker {} cadastrado com sucesso", talker.getId());
    }
}
