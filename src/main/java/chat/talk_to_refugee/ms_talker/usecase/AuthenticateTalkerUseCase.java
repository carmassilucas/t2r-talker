package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.AuthRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthenticateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateTalkerUseCase.class);

    private final TalkerRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final String applicationName;

    public AuthenticateTalkerUseCase(TalkerRepository repository,
                                     PasswordEncoder passwordEncoder,
                                     JwtEncoder jwtEncoder,
                                     @Value("${spring.application.name}") String applicationName) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.applicationName = applicationName;
    }

    public AuthResponse execute(AuthRequest requestBody) {
        log.info("Solicitação de autenticação do talker com e-mail {}", requestBody.email());
        var talker = this.repository.findByEmail(requestBody.email()).orElseThrow(() -> {
            log.warn("Não autenticado, e-mail não encontrado");
            return new BadCredentialsException("e-mail or password is invalid");
        });

        if (!this.passwordEncoder.matches(requestBody.password(), talker.getPassword())) {
            log.warn("Não autenticado, senha incorreta");
            throw new BadCredentialsException("e-mail or password is invalid");
        }

        var now = Instant.now();
        var expiresIn = 3600L;

        var claims = JwtClaimsSet.builder()
                .issuer(this.applicationName)
                .subject(talker.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        var parameters = JwtEncoderParameters.from(claims);
        var token = this.jwtEncoder.encode(parameters).getTokenValue();

        log.info("Talker {} autenticado com sucesso, retornando token", talker.getId());
        return new AuthResponse(token, expiresIn);
    }
}
