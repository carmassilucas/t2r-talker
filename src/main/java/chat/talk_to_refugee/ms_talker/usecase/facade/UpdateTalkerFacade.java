package chat.talk_to_refugee.ms_talker.usecase.facade;

import chat.talk_to_refugee.ms_talker.mapper.UpdateTalkerMapper;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.validator.UpdateTalkerValidator;
import org.springframework.stereotype.Component;

@Component
public record UpdateTalkerFacade(TalkerRepository repository,
                                 UpdateTalkerValidator validator,
                                 UpdateTalkerMapper mapper) {
}
