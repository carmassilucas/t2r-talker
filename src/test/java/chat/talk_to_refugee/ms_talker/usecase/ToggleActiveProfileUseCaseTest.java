package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.usecase.facade.ToggleActiveProfileFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleActiveProfileUseCaseTest {

    @InjectMocks
    private ToggleActiveProfileUseCase toggleActiveProfile;

    @Mock private ToggleActiveProfileFacade dependencies;
    @Mock private TalkerRepository repository;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Deve ser possível ativar ou desativar perfil")
    void should_be_possible_activate_or_deactivate_profile(Boolean activate) {
        when(this.dependencies.repository()).thenReturn(this.repository);

        var uuid = UUID.randomUUID();

        var talker = new Talker();
        talker.setId(uuid);
        talker.setActive(!activate);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));

        this.toggleActiveProfile.execute(uuid, activate);

        verify(this.repository).save(talker);
    }
}