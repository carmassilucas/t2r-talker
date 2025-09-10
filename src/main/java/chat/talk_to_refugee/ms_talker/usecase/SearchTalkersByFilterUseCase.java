package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.exception.InvalidDataException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.resource.dto.InvalidParam;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersResponse;
import chat.talk_to_refugee.ms_talker.usecase.facade.SearchTalkersByFilterFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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

        handleInvalidParam(this.dependencies.locationValidator().validate(
                requestParams.currentlyState(), requestParams.currentlyCity()
        ));

        var type = Optional.ofNullable(requestParams.type())
                .orElse(Collections.emptyList())
                .stream()
                .map(t -> {
                    handleInvalidParam(this.dependencies.typeValidator().validate(t));
                    return TalkerType.Values.valueOf(t.toUpperCase()).getId();
                })
                .toList();

        var page = requestParams.page() == null ? 0 : requestParams.page();
        var size = requestParams.size() == null ? 10 : requestParams.size();

        var talker = this.dependencies.repository().findByNonBlankFilters(
                id,
                requestParams.fullName(),
                requestParams.currentlyState(),
                requestParams.currentlyCity(),
                type,
                PageRequest.of(page, size)
        ).map(this.dependencies.mapper()::toSearchResponse);

        log.info("Returning talkers, {} records were found", talker.getTotalElements());

        return talker;
    }

    private void handleInvalidParam(Optional<InvalidParam> param) {
        param.ifPresent(invalidParam -> {
            log.warn("Your request parameters didn't validate: {}", invalidParam);
            throw new InvalidDataException(List.of(invalidParam));
        });
    }
}
