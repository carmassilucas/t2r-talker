package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.mapper.UpdateTalkerMapper;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import chat.talk_to_refugee.ms_talker.validator.UpdateTalkerValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTalkerUseCaseTest {

    @InjectMocks
    private UpdateTalkerUseCase updateTalker;

    @Mock
    private TalkerRepository repository;

    @Mock
    private UpdateTalkerValidator validator;

    @Mock
    private UpdateTalkerMapper mapper;

    @Test
    @DisplayName("Deve ser possível atualizar informações")
    void should_be_possible_update_talker_information() {
        var uuid = UUID.randomUUID();
        var requestBody = mock(UpdateTalker.class);
        var talker = mock(Talker.class);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));
        when(this.mapper.updateTalker(requestBody, talker)).thenReturn(talker);

        this.updateTalker.execute(uuid, requestBody);

        verify(this.validator).validate(requestBody);
        verify(this.mapper).updateTalker(requestBody, talker);
        verify(this.repository).save(talker);
    }

    @Test
    @DisplayName("Deve não ser possível atualizar informações quando talker não encontrado")
    void should_not_be_possible_update_talker_information_when_talker_not_found() {
        var uuid = UUID.randomUUID();
        var requestBody = mock(UpdateTalker.class);

        when(this.repository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(TalkerNotFoundException.class, () -> this.updateTalker.execute(uuid, requestBody));

        verify(this.validator, never()).validate(requestBody);
        verify(this.mapper, never()).updateTalker(eq(requestBody), any(Talker.class));
        verify(this.repository, never()).save(any(Talker.class));
    }
}