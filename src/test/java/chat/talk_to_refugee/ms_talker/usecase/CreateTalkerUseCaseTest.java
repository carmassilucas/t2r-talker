package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.TalkerAlreadyExistsException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import chat.talk_to_refugee.ms_talker.usecase.facade.CreateTalkerFacade;
import chat.talk_to_refugee.ms_talker.validator.CreateTalkerValidator;
import org.junit.jupiter.api.DisplayName;
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
class CreateTalkerUseCaseTest {

    @InjectMocks
    private CreateTalkerUseCase createTalker;

    @Mock private CreateTalkerFacade dependencies;
    @Mock private TalkerRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CreateTalkerValidator validator;

    @Test
    @DisplayName("Deve ser possível criar um novo talker")
    void should_be_possible_create_new_talker() {
        when(this.dependencies.validator()).thenReturn(this.validator);
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.passwordEncoder()).thenReturn(this.passwordEncoder);

        var requestBody = new CreateTalker(
                "full name",
                "2000-01-01",
                "test@email.com",
                "password",
                "collaborator"
        );

        var talker = new Talker(
                requestBody.fullName(),
                LocalDate.parse(requestBody.birthDate()),
                requestBody.email(),
                requestBody.password(),
                TalkerType.Values.valueOf(requestBody.type().toUpperCase()).get()
        );

        when(this.repository.findByEmail(requestBody.email())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(requestBody.password())).thenReturn(requestBody.password());

        this.createTalker.execute(requestBody);

        verify(this.validator).validate(requestBody);
        verify(this.passwordEncoder).encode(requestBody.password());
        verify(this.repository).save(talker);
    }

    @Test
    @DisplayName("Deve não ser possível criar um novo talker quando e-mail já existe")
    void should_not_be_possible_create_new_talker_when_email_already_exists() {
        when(this.dependencies.validator()).thenReturn(this.validator);
        when(this.dependencies.repository()).thenReturn(this.repository);

        var requestBody = new CreateTalker(
                "full name",
                "2000-01-01",
                "test@email.com",
                "password",
                "collaborator"
        );

        var talker = mock(Talker.class);
        when(this.repository.findByEmail(requestBody.email())).thenReturn(Optional.of(talker));

        assertThrows(TalkerAlreadyExistsException.class, () -> this.createTalker.execute(requestBody));

        verify(this.validator).validate(requestBody);
        verifyNoMoreInteractions(this.repository, this.passwordEncoder, this.validator);
    }
}