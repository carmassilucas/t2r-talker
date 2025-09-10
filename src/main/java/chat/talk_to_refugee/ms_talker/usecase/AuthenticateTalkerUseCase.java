package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.resource.dto.AuthRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.AuthResponse;
import chat.talk_to_refugee.ms_talker.usecase.facade.AuthenticateTalkerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthenticateTalkerUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateTalkerUseCase.class);

    private final AuthenticateTalkerFacade dependencies;

    public AuthenticateTalkerUseCase(AuthenticateTalkerFacade dependencies) {
        this.dependencies = dependencies;
    }

    public AuthResponse execute(AuthRequest requestBody) {
        log.info("Trying to authenticate talker");

        var talker = this.dependencies.repository().findByEmail(requestBody.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail or password is invalid"));

        if (!this.dependencies.passwordEncoder().matches(requestBody.password(), talker.getPassword())) {
            throw new BadCredentialsException("E-mail or password is invalid");
        }

        var now = Instant.now();
        var expiresIn = 3600L;

        var claims = JwtClaimsSet.builder()
                .issuer(this.dependencies.applicationName())
                .subject(talker.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        var parameters = JwtEncoderParameters.from(claims);
        var token = this.dependencies.jwtEncoder().encode(parameters).getTokenValue();

        log.info("Authenticated talker, responding token");
        return new AuthResponse(token, expiresIn);
    }
}
