package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.AuthRequest;
import chat.talk_to_refugee.ms_talker.usecase.facade.AuthenticateTalkerFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateTalkerUseCaseTest {

    @InjectMocks
    private AuthenticateTalkerUseCase authenticateTalker;

    @Mock private AuthenticateTalkerFacade dependencies;
    @Mock private TalkerRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtEncoder jwtEncoder;

    @Test
    @DisplayName("Deve ser possível recuperar token de autenticação")
    void should_be_possible_retrieve_authentication_token() {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.passwordEncoder()).thenReturn(this.passwordEncoder);
        when(this.dependencies.jwtEncoder()).thenReturn(this.jwtEncoder);
        when(this.dependencies.applicationName()).thenReturn("application-name");

        var requestBody = new AuthRequest("teste@email.com", "password");

        var talker = new Talker();
        talker.setPassword("password");
        talker.setId(UUID.randomUUID());

        var jwt = mock(Jwt.class);

        when(this.repository.findByEmail(requestBody.email())).thenReturn(Optional.of(talker));
        when(this.passwordEncoder.matches(requestBody.password(), talker.getPassword())).thenReturn(true);
        when(this.jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("token");

        var response = this.authenticateTalker.execute(requestBody);

        assertNotNull(response);
        assertEquals("token", response.accessToken());

        verify(this.repository).findByEmail(requestBody.email());
        verify(this.passwordEncoder).matches(requestBody.password(), talker.getPassword());
        verify(this.jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando talker não encontrado")
    void should_throw_exception_when_talker_not_found() {
        when(this.dependencies.repository()).thenReturn(this.repository);

        var requestBody = new AuthRequest("teste@email.com", "password");

        when(this.repository.findByEmail(requestBody.email())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> this.authenticateTalker.execute(requestBody));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta")
    void should_throw_exception_when_talker_password_incorrect() {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.passwordEncoder()).thenReturn(this.passwordEncoder);

        var requestBody = new AuthRequest("teste@email.com", "password");

        var talker = new Talker();
        talker.setPassword("password");

        when(this.repository.findByEmail(requestBody.email())).thenReturn(Optional.of(talker));
        when(this.passwordEncoder.matches(requestBody.password(), talker.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> this.authenticateTalker.execute(requestBody));
    }

}