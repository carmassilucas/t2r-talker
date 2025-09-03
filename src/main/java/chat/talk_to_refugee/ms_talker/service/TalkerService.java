package chat.talk_to_refugee.ms_talker.service;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.*;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TalkerService {

    private static final Logger log = LoggerFactory.getLogger(TalkerService.class);

    private final TalkerRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    private final String applicationName;

    public TalkerService(TalkerRepository repository,
                         PasswordEncoder passwordEncoder,
                         JwtEncoder jwtEncoder,
                         @Value("${spring.application.name}") String applicationName) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.applicationName = applicationName;
    }

    @Transactional
    public void create(CreateTalker requestBody) {
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

    public AuthResponse auth(AuthRequest requestBody) {
        var talker = this.repository.findByEmail(requestBody.email()).orElseThrow(() ->
                new BadCredentialsException("user or password is invalid")
        );

        if (!this.passwordEncoder.matches(requestBody.password(), talker.getPassword())) {
            throw new BadCredentialsException("user or password is invalid");
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

        return new AuthResponse(token, expiresIn);
    }

    public TalkerProfile profile(UUID id) {
        var talker = this.repository.findById(id).orElseThrow(TalkerNotFoundException::new);

        return new TalkerProfile(
                talker.getId(),
                talker.getFullName(),
                talker.getProfilePhoto(),
                talker.getAboutMe(),
                talker.getBirthDate(),
                talker.getCurrentlyCity(),
                talker.getCurrentlyState(),
                talker.getEmail(),
                talker.getType()
        );
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
