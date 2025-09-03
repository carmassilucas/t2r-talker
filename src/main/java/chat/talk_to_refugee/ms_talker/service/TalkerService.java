package chat.talk_to_refugee.ms_talker.service;

import chat.talk_to_refugee.ms_talker.exception.PasswordNotMatchException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdatedPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TalkerService {

    private final TalkerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public TalkerService(TalkerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updatePassword(UUID id, UpdatedPassword requestBody) {
        var talker = this.repository.findById(id).orElseThrow(TalkerNotFoundException::new);

        if (!this.passwordEncoder.matches(requestBody.currentPassword(), talker.getPassword())) {
            throw new PasswordNotMatchException();
        }

        var password = this.passwordEncoder.encode(requestBody.newPassword());
        talker.setPassword(password);

        this.repository.save(talker);
    }
}
