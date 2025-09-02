package chat.talk_to_refugee.ms_talker.service;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.exception.TypeNotFoundException;
import chat.talk_to_refugee.ms_talker.exception.UnderageException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.AuthRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TalkerServiceTest {

    @InjectMocks
    private TalkerService service;

    @Mock
    private TalkerRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @Nested
    class CreateTest {

        @Test
        @DisplayName("Deve ser possível criar um novo talker")
        void should_be_possible_create_new_talker() {
            var requestBody = new CreateTalker(
                    "full name",
                    "2000-01-01",
                    "test@email.com",
                    "password",
                    "collaborator"
            );

            when(repository.findByEmail(requestBody.email())).thenReturn(Optional.empty());

            service.create(requestBody);

            verify(passwordEncoder).encode(requestBody.password());
            verify(repository).save(any(Talker.class));
        }

        @Test
        @DisplayName("Deve não ser possível criar um novo talker quando menor de idade")
        void should_not_be_possible_create_new_talker_when_underage() {
            var requestBody = new CreateTalker(
                    "full name",
                    "2020-01-01",
                    "test@email.com",
                    "password",
                    "collaborator"
            );

            assertThrows(UnderageException.class, () -> service.create(requestBody));

            verify(passwordEncoder, never()).encode(requestBody.password());
            verify(repository, never()).save(any(Talker.class));
        }

        @Test
        @DisplayName("Deve não ser possível criar um novo talker quando e-mail já existe")
        void should_not_be_possible_create_new_talker_when_email_already_exists() {
            var requestBody = new CreateTalker(
                    "full name",
                    "2000-01-01",
                    "test@email.com",
                    "password",
                    "collaborator"
            );

            when(repository.findByEmail(requestBody.email())).thenReturn(
                    Optional.of(mock(Talker.class))
            );

            assertThrows(TalkerAlreadyExistsException.class, () -> service.create(requestBody));

            verify(passwordEncoder, never()).encode(requestBody.password());
            verify(repository, never()).save(any(Talker.class));
        }

        @Test
        @DisplayName("Deve não ser possível criar um novo talker quando tipo inválido")
        void should_not_be_possible_create_new_talker_when_invalid_type() {
            var requestBody = new CreateTalker(
                    "full name",
                    "2000-01-01",
                    "test@email.com",
                    "password",
                    "invalid-type"
            );

            assertThrows(TypeNotFoundException.class, () -> service.create(requestBody));

            verify(passwordEncoder, never()).encode(requestBody.password());
            verify(repository, never()).save(any(Talker.class));
        }
    }

    @Nested
    class AuthTest {

        @BeforeEach
        void set_up() {
            service = new TalkerService(repository, passwordEncoder, jwtEncoder, "ms-talker");
        }

        @Test
        @DisplayName("Deve ser possível recuperar token de autenticação")
        void should_be_possible_retrieve_authentication_token() {
            var requestBody = new AuthRequest("teste@email.com", "password");
            
            var talker = new Talker();
            talker.setPassword("password");
            talker.setId(UUID.randomUUID());

            var jwt = mock(Jwt.class);
            
            when(repository.findByEmail(requestBody.email())).thenReturn(Optional.of(talker));
            when(passwordEncoder.matches(requestBody.password(), talker.getPassword())).thenReturn(true);
            when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
            when(jwt.getTokenValue()).thenReturn("token");

            var response = service.auth(requestBody);

            assertNotNull(response);
            assertEquals("token", response.accessToken());

            verify(repository).findByEmail(requestBody.email());
            verify(passwordEncoder).matches(requestBody.password(), talker.getPassword());
            verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando talker não encontrado")
        void should_throw_exception_when_talker_not_found() {
            var requestBody = new AuthRequest("teste@email.com", "password");

            when(repository.findByEmail(requestBody.email())).thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class, () -> service.auth(requestBody));
        }

        @Test
        @DisplayName("Deve lançar exceção quando senha incorreta")
        void should_throw_exception_when_talker_password_incorrect() {
            var requestBody = new AuthRequest("teste@email.com", "password");

            var talker = new Talker();
            talker.setPassword("password");

            when(repository.findByEmail(requestBody.email())).thenReturn(Optional.of(talker));
            when(passwordEncoder.matches(requestBody.password(), talker.getPassword())).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> service.auth(requestBody));
        }
    }

    @Nested
    class ProfileTest {

        @Test
        @DisplayName("Deve ser possível recuperar perfil do talker")
        void should_be_possible_retrieve_talker_profile() {
            var uuid = UUID.randomUUID();
            var talker = new Talker();
            talker.setId(uuid);

            when(repository.findById(uuid)).thenReturn(Optional.of(talker));

            var profile = service.profile(uuid);

            assertNotNull(profile);
            assertEquals(uuid, profile.id());
        }

        @Test
        @DisplayName("Deve lançar exceção quando talker não encontrado")
        void should_throw_exception_when_talker_not_found() {
            when(repository.findById(any())).thenReturn(Optional.empty());

            assertThrows(TalkerNotFoundException.class, () -> service.profile(UUID.randomUUID()));
        }
    }
}
