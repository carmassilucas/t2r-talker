package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.TalkerProfile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TalkerProfileUseCase {

    private final TalkerRepository repository;

    public TalkerProfileUseCase(TalkerRepository repository) {
        this.repository = repository;
    }

    public TalkerProfile execute(UUID id) {
        var talker = this.repository.findById(id).orElseThrow(TalkerNotFoundException::new);

        return new TalkerProfile(
                talker.getId(),
                talker.getFullName(),
                talker.getProfilePhoto(),
                talker.getAboutMe(),
                talker.getBirthDate(),
                talker.getCurrentlyCity(),
                talker.getCurrentlyState(),
                talker.getEmail(),
                talker.getType()
        );
    }
}
