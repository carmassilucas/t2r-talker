package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersResponse;
import chat.talk_to_refugee.ms_talker.usecase.facade.SearchTalkersByFilterFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class SearchTalkersByFilterUseCase {

    private static final Logger log = LoggerFactory.getLogger(SearchTalkersByFilterUseCase.class);

    private final SearchTalkersByFilterFacade dependencies;

    public SearchTalkersByFilterUseCase(SearchTalkersByFilterFacade dependencies) {
        this.dependencies = dependencies;
    }

    public Page<SearchTalkersResponse> execute(UUID id, SearchTalkersRequest requestParams) {
        log.info("Searching for talkers by filtering by: {}", requestParams);

        this.dependencies.repository().findById(id).orElseThrow(TalkerNotFoundException::new);

        this.dependencies.validator().validate(requestParams);

        var types = Optional.ofNullable(requestParams.type())
                .orElse(Collections.emptyList())
                .stream()
                .map(t -> TalkerType.Values.valueOf(t.toUpperCase()).getId())
                .toList();

        var page = requestParams.page() == null ? 0 : requestParams.page();
        var size = requestParams.size() == null ? 10 : requestParams.size();

        var talker = this.dependencies.repository().findByNonBlankFilters(
                id,
                requestParams.fullName(),
                requestParams.currentlyState(),
                requestParams.currentlyCity(),
                types,
                PageRequest.of(page, size)
        ).map(this.dependencies.mapper()::toSearchResponse);

        log.info("Returning talkers, {} records were found", talker.getTotalElements());

        return talker;
    }
}
