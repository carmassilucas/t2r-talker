package chat.talk_to_refugee.ms_talker.resource.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateProfilePhoto(@NotNull MultipartFile profilePhoto) {
}
