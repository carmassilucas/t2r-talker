package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.PasswordNotMatchException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdatedPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateTalkerPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateTalkerPasswordUseCase.class);

    private final TalkerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UpdateTalkerPasswordUseCase(TalkerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(UUID id, UpdatedPassword requestBody) {
        log.info("Atualizando senha do talker {}", id);
        var talker = this.repository.findById(id).orElseThrow(() -> {
            log.warn("Talker {} não encontrado", id);
            return new TalkerNotFoundException();
        });

        if (!this.passwordEncoder.matches(requestBody.currentPassword(), talker.getPassword())) {
            log.warn("Senha atual incorreta fornecida para talker {}", id);
            throw new PasswordNotMatchException();
        }

        var password = this.passwordEncoder.encode(requestBody.newPassword());
        talker.setPassword(password);

        this.repository.save(talker);
        log.info("Senha do talker {} atualizada", id);
    }
}
