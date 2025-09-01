package chat.talk_to_refugee.ms_talker.service;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.exception.UnderageException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TalkerService {

    private static final Logger log = LoggerFactory.getLogger(TalkerService.class);

    private final TalkerRepository repository;
    private final PasswordEncoder encoder;

    public TalkerService(TalkerRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public void create(CreateTalker requestBody) {
        log.info("Solicitação de cadastro de usuário {}", requestBody.email());

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
                encoder.encode(requestBody.password())
        );

        this.repository.save(talker);
        log.info("Talker {} cadastrado com sucesso", requestBody.email());
    }
}
