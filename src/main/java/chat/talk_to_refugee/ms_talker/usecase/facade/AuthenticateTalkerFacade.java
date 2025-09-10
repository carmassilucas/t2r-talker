package chat.talk_to_refugee.ms_talker.usecase.facade;

import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Component;

@Component
public record AuthenticateTalkerFacade(TalkerRepository repository,
                                       PasswordEncoder passwordEncoder,
                                       JwtEncoder jwtEncoder,
                                       @Value("${spring.application.name}") String applicationName) {
}
