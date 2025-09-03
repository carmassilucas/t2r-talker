package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.exception.TypeNotFoundException;
import chat.talk_to_refugee.ms_talker.exception.UnderageException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CreateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateTalkerUseCase.class);

    private final TalkerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CreateTalkerUseCase(TalkerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(CreateTalker requestBody) {
        log.info("Solicitação de cadastro de usuário {}", requestBody.email());

        try {
            var type = TalkerType.Values.valueOf(
                    requestBody.type().toUpperCase()
            );

            var birthDate = LocalDate.parse(requestBody.birthDate());
            if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
                log.warn("Não é permitido o cadastro de menores de idade");
                throw new UnderageException();
            }

            this.repository.findByEmail(requestBody.email()).ifPresent(talker -> {
                log.warn("Endereço de e-mail {} já cadastrado", requestBody.email());
                throw new TalkerAlreadyExistsException();
            });

            var talker = new Talker(
                    requestBody.fullName(),
                    birthDate,
                    requestBody.email(),
                    this.passwordEncoder.encode(requestBody.password()),
                    type.get()
            );

            this.repository.save(talker);
            log.info("Talker {} cadastrado com sucesso", requestBody.email());
        } catch (IllegalArgumentException ex) {
            throw new TypeNotFoundException();
        }
    }
}
