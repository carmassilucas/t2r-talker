package chat.talk_to_refugee.ms_talker.service;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.exception.UnderageException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TalkerServiceTest {

    @InjectMocks
    private TalkerService service;

    @Mock
    private TalkerRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Nested
    class CreateTest {

        @Test
        @DisplayName("Deve ser possível criar um novo talker")
        void should_be_possible_create_new_talker() {
            var requestBody = new CreateTalker(
                    "full name",
                    "2000-01-01",
                    "test@email.com",
                    "password"
            );
            var birthDate = LocalDate.parse(requestBody.birthDate());

            when(repository.findByEmail(requestBody.email())).thenReturn(Optional.empty());

            service.create(requestBody);

            verify(encoder).encode(requestBody.password());
            verify(repository).save(new Talker(
                    requestBody.fullName(),
                    birthDate,
                    requestBody.email(),
                    encoder.encode(requestBody.password())
            ));
        }

        @Test
        @DisplayName("Deve não ser possível criar um novo talker quando menor de idade")
        void should_not_be_possible_create_new_talker_when_underage() {
            var requestBody = new CreateTalker(
                    "full name",
                    "2020-01-01",
                    "test@email.com",
                    "password"
            );
            var birthDate = LocalDate.parse(requestBody.birthDate());

            assertThrows(UnderageException.class, () -> service.create(requestBody));

            verify(encoder, never()).encode(requestBody.password());
            verify(repository, never()).save(new Talker(
                    requestBody.fullName(),
                    birthDate,
                    requestBody.email(),
                    encoder.encode(requestBody.password())
            ));
        }

        @Test
        @DisplayName("Deve não ser possível criar um novo talker quando e-mail já existe")
        void should_not_be_possible_create_new_talker_when_email_already_exists() {
            var requestBody = new CreateTalker(
                    "full name",
                    "2000-01-01",
                    "test@email.com",
                    "password"
            );
            var birthDate = LocalDate.parse(requestBody.birthDate());
            var talker = new Talker(
                    requestBody.fullName(),
                    birthDate,
                    requestBody.email(),
                    encoder.encode(requestBody.password())
            );

            when(repository.findByEmail(requestBody.email())).thenReturn(Optional.of(talker));

            assertThrows(TalkerAlreadyExistsException.class, () -> service.create(requestBody));

            verify(encoder).encode(requestBody.password());
            verify(repository, never()).save(new Talker(
                    requestBody.fullName(),
                    birthDate,
                    requestBody.email(),
                    encoder.encode(requestBody.password())
            ));
        }
    }
}
