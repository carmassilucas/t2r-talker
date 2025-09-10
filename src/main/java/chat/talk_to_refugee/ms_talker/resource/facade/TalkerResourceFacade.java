package chat.talk_to_refugee.ms_talker.resource.facade;

import chat.talk_to_refugee.ms_talker.usecase.*;
import org.springframework.stereotype.Component;

@Component
public record TalkerResourceFacade(
        CreateTalkerUseCase create,
        AuthenticateTalkerUseCase authenticate,
        TalkerProfileUseCase profile,
        UpdateTalkerUseCase update,
        UpdatePasswordUseCase updatePassword,
        UpdateProfilePhotoUseCase updatePhoto,
        SearchTalkersByFilterUseCase search,
        ToggleActiveProfileUseCase toggleActive
) {}

