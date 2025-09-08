package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ToggleActiveProfileUseCase {
    
    private final TalkerRepository repository;

    public ToggleActiveProfileUseCase(TalkerRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id, Boolean activate) {
        var talker = repository.findById(id)
                .orElseThrow(TalkerNotFoundException::new);

        if (!talker.getActive().equals(activate)) {
            talker.setActive(activate);
            this.repository.save(talker);
        }
    }
}
