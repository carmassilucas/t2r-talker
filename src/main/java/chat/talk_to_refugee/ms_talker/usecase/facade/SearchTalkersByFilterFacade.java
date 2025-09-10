package chat.talk_to_refugee.ms_talker.usecase.facade;

import chat.talk_to_refugee.ms_talker.mapper.SearchTalkersByFilterMapper;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.validator.SearchTalkersByFilterValidator;
import org.springframework.stereotype.Component;

@Component
public record SearchTalkersByFilterFacade(TalkerRepository repository,
                                          SearchTalkersByFilterValidator validator,
                                          SearchTalkersByFilterMapper mapper) {
}
