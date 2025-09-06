package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import chat.talk_to_refugee.ms_talker.validator.CreateTalkerValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTalkerUseCaseTest {

    @InjectMocks
    private CreateTalkerUseCase createTalker;

    @Mock
    private TalkerRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CreateTalkerValidator validator;

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

        var talker = mock(Talker.class);

        when(this.repository.findByEmail(requestBody.email())).thenReturn(Optional.empty());
        when(this.repository.save(any(Talker.class))).thenReturn(talker);
        when(talker.getId()).thenReturn(UUID.randomUUID());

        this.createTalker.execute(requestBody);

        verify(this.validator).validate(requestBody);
        verify(this.passwordEncoder).encode(requestBody.password());
        verify(this.repository).save(any(Talker.class));
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

        when(this.repository.findByEmail(requestBody.email())).thenReturn(
                Optional.of(mock(Talker.class))
        );

        assertThrows(TalkerAlreadyExistsException.class, () -> this.createTalker.execute(requestBody));

        verify(this.validator).validate(requestBody);
        verify(this.passwordEncoder, never()).encode(requestBody.password());
        verify(this.repository, never()).save(any(Talker.class));
    }
}