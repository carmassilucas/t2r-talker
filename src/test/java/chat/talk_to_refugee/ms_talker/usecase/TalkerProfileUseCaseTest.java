package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalkerProfileUseCaseTest {

    @InjectMocks
    private TalkerProfileUseCase talkerProfile;

    @Mock
    private TalkerRepository repository;

    @Test
    @DisplayName("Deve ser possível recuperar perfil do talker")
    void should_be_possible_retrieve_talker_profile() {
        var uuid = UUID.randomUUID();
        var talker = new Talker();
        talker.setId(uuid);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));

        var profile = this.talkerProfile.execute(uuid);

        assertNotNull(profile);
        assertEquals(uuid, profile.id());
    }

    @Test
    @DisplayName("Deve lançar exceção quando talker não encontrado")
    void should_throw_exception_when_talker_not_found() {
        when(this.repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(TalkerNotFoundException.class, () -> this.talkerProfile.execute(UUID.randomUUID()));
    }
}