package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.PasswordNotMatchException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdatedPassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTalkerPasswordUseCaseTest {

    @InjectMocks
    private UpdateTalkerPasswordUseCase updateTalkerPassword;

    @Mock
    private TalkerRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve ser possível atualizar a senha")
    void should_be_possible_update_talker_password() {
        var uuid = UUID.randomUUID();

        var talker = new Talker();
        talker.setId(uuid);

        var requestBody = new UpdatedPassword("current password", "new password");

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));
        when(this.passwordEncoder.matches(requestBody.currentPassword(), talker.getPassword())).thenReturn(true);
        when(this.passwordEncoder.encode(requestBody.newPassword())).thenReturn("new password");

        this.updateTalkerPassword.execute(uuid, requestBody);

        verify(this.repository).save(talker);
    }

    @Test
    @DisplayName("Deve lançar exceção quando talker não encontrado")
    void should_throw_exception_when_talker_not_found() {
        var uuid =  UUID.randomUUID();
        when(this.repository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(TalkerNotFoundException.class, () -> this.updateTalkerPassword.execute(uuid, mock(UpdatedPassword.class)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta")
    void should_throw_exception_when_talker_password_incorrect() {
        var uuid = UUID.randomUUID();

        var talker = new Talker();
        talker.setId(uuid);
        talker.setPassword("current password");

        var requestBody = new UpdatedPassword("current password", "new password");

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));
        when(this.passwordEncoder.matches(requestBody.currentPassword(), talker.getPassword())).thenReturn(false);

        assertThrows(PasswordNotMatchException.class, () -> this.updateTalkerPassword.execute(uuid, requestBody));
    }
}
