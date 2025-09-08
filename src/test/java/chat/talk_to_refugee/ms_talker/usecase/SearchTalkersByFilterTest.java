package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.mapper.SearchTalkersByFilterMapper;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersResponse;
import chat.talk_to_refugee.ms_talker.validator.common.LocationValidator;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchTalkersByFilterTest {

    @InjectMocks
    private SearchTalkersByFilterUseCase searchTalkersByFilter;

    @Mock
    private TalkerRepository repository;

    @Mock
    private LocationValidator locationValidator;

    @Mock
    private TypeValidator typeValidator;

    @Mock
    private SearchTalkersByFilterMapper mapper;

    @Test
    @DisplayName("Deve ser possível buscar talkers por filtros")
    void should_be_possible_search_talkers_by_filter() {
        var uuid = UUID.randomUUID();
        var requestParams = new SearchTalkersRequest(
                "full name", "currently state", "currently city", List.of("collaborator"), 0, 10
        );
        var talker = mock(Talker.class);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));
        when(this.locationValidator.validate(
                requestParams.currentlyState(), requestParams.currentlyCity()
        )).thenReturn(Optional.empty());
        when(this.typeValidator.validate(requestParams.type().getFirst())).thenReturn(Optional.empty());
        when(this.repository.findByNonBlankFilters(
                uuid,
                requestParams.fullName(),
                requestParams.currentlyState(),
                requestParams.currentlyCity(),
                List.of(1L),
                PageRequest.of(requestParams.page(), requestParams.size())
        )).thenReturn(new PageImpl<>(List.of(talker)));
        when(this.mapper.toSearchResponse(talker)).thenReturn(mock(SearchTalkersResponse.class));

        this.searchTalkersByFilter.execute(uuid, requestParams);

        verify(this.locationValidator).validate(requestParams.currentlyState(), requestParams.currentlyCity());
        verify(this.typeValidator).validate(requestParams.type().getFirst());
        verify(this.repository).findByNonBlankFilters(
                uuid,
                requestParams.fullName(),
                requestParams.currentlyState(),
                requestParams.currentlyCity(),
                List.of(1L),
                PageRequest.of(requestParams.page(), requestParams.size())
        );
        verify(this.mapper).toSearchResponse(talker);
    }

    @Test
    @DisplayName("Deve lançar exceção quando talker não encontrado")
    void should_throw_exception_when_talker_not_found() {
        when(this.repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TalkerNotFoundException.class,
                () -> this.searchTalkersByFilter.execute(UUID.randomUUID(), mock(SearchTalkersRequest.class))
        );

        verify(this.locationValidator, never()).validate(anyString(), anyString());
        verify(this.typeValidator, never()).validate(any());
        verify(this.repository, never()).findByNonBlankFilters(
                any(UUID.class), anyString(), anyString(), anyString(), anyList(), any(PageRequest.class)
        );
        verify(this.mapper, never()).toSearchResponse(any(Talker.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando localização inválida")
    void should_throw_exception_when_invalid_location() {
        var uuid = UUID.randomUUID();
        var requestParams = new SearchTalkersRequest(
                "full name", "currently state", "currently city", List.of("collaborator"), 0, 10
        );
        var talker = mock(Talker.class);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));
        doThrow(InvalidDataException.class).when(this.locationValidator).validate(
                requestParams.currentlyState(), requestParams.currentlyCity()
        );

        assertThrows(InvalidDataException.class, () -> this.searchTalkersByFilter.execute(uuid, requestParams));

        verify(this.locationValidator).validate(anyString(), anyString());
        verify(this.typeValidator, never()).validate(any());
        verify(this.repository, never()).findByNonBlankFilters(
                any(UUID.class), anyString(), anyString(), anyString(), anyList(), any(PageRequest.class)
        );
        verify(this.mapper, never()).toSearchResponse(any(Talker.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo inválido")
    void should_throw_exception_when_invalid_type() {
        var uuid = UUID.randomUUID();
        var requestParams = new SearchTalkersRequest(
                "full name", "currently state", "currently city", List.of("collaborator"), 0, 10
        );
        var talker = mock(Talker.class);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));
        doThrow(InvalidDataException.class).when(this.typeValidator).validate("collaborator");

        assertThrows(InvalidDataException.class, () -> this.searchTalkersByFilter.execute(uuid, requestParams));

        verify(this.locationValidator).validate(anyString(), anyString());
        verify(this.typeValidator).validate(any());
        verify(this.repository, never()).findByNonBlankFilters(
                any(UUID.class), anyString(), anyString(), anyString(), anyList(), any(PageRequest.class)
        );
        verify(this.mapper, never()).toSearchResponse(any(Talker.class));
    }
}