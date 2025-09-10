package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.PasswordNotMatchException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdatedPassword;
import chat.talk_to_refugee.ms_talker.usecase.facade.UpdatePasswordFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdatePasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdatePasswordUseCase.class);

    private final UpdatePasswordFacade dependencies;

    public UpdatePasswordUseCase(UpdatePasswordFacade dependencies) {
        this.dependencies = dependencies;
    }

    @Transactional
    public void execute(UUID id, UpdatedPassword requestBody) {
        log.info("Updating talker password");

        var talker = this.dependencies.repository().findById(id)
                .orElseThrow(TalkerNotFoundException::new);

        if (!this.dependencies.passwordEncoder().matches(requestBody.currentPassword(), talker.getPassword())) {
            throw new PasswordNotMatchException();
        }

        var password = this.dependencies.passwordEncoder().encode(requestBody.newPassword());
        talker.setPassword(password);

        this.dependencies.repository().save(talker);

        log.info("Update done");
    }
}
