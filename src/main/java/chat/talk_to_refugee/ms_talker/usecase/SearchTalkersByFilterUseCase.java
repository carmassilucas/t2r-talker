package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.mapper.SearchTalkersByFilterMapper;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersResponse;
import chat.talk_to_refugee.ms_talker.validator.common.LocationValidator;
import chat.talk_to_refugee.ms_talker.validator.common.TypeValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchTalkersByFilterUseCase {

    private final TalkerRepository repository;
    private final LocationValidator locationValidator;
    private final TypeValidator typeValidator;
    private final SearchTalkersByFilterMapper mapper;

    public SearchTalkersByFilterUseCase(TalkerRepository repository,
                                        LocationValidator locationValidator,
                                        TypeValidator typeValidator,
                                        SearchTalkersByFilterMapper mapper) {
        this.repository = repository;
        this.locationValidator = locationValidator;
        this.typeValidator = typeValidator;
        this.mapper = mapper;
    }

    public Page<SearchTalkersResponse> execute(UUID id, SearchTalkersRequest requestParams) {
        this.repository.findById(id).orElseThrow(TalkerNotFoundException::new);

        this.locationValidator.validate(requestParams.currentlyState(), requestParams.currentlyCity())
                .ifPresent(invalidParam -> {
                    throw new InvalidDataException(List.of(invalidParam));
                });

        var page = requestParams.page() == null ? 0 : requestParams.page();
        var size = requestParams.size() == null ? 10 : requestParams.size();

        var type = Optional.ofNullable(requestParams.type())
                .orElse(Collections.emptyList())
                .stream()
                .map(t -> {
                    this.typeValidator.validate(t).ifPresent(invalidParam -> {
                        throw new InvalidDataException(List.of(invalidParam));
                    });
                    return TalkerType.Values.valueOf(t.toUpperCase()).getId();
                })
                .toList();

        return repository.findByNonBlankFilters(
                id,
                requestParams.fullName(),
                requestParams.currentlyState(),
                requestParams.currentlyCity(),
                type,
                PageRequest.of(page, size)
        ).map(mapper::toSearchResponse);
    }
}
