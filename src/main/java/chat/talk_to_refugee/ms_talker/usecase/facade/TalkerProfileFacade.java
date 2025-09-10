package chat.talk_to_refugee.ms_talker.usecase.facade;

import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import org.springframework.stereotype.Component;

@Component
public record TalkerProfileFacade(TalkerRepository repository) {
}
