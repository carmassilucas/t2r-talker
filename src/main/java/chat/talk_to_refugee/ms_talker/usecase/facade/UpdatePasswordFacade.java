package chat.talk_to_refugee.ms_talker.usecase.facade;

import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public record UpdatePasswordFacade(TalkerRepository repository, PasswordEncoder passwordEncoder) {
}
