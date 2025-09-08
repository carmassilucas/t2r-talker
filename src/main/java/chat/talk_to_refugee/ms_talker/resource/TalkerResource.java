package chat.talk_to_refugee.ms_talker.resource;

import chat.talk_to_refugee.ms_talker.resource.dto.*;
import chat.talk_to_refugee.ms_talker.usecase.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/talkers")
public class TalkerResource {

    private final CreateTalkerUseCase createTalker;
    private final AuthenticateTalkerUseCase authenticateTalker;
    private final TalkerProfileUseCase talkerProfile;
    private final UpdateTalkerUseCase updateTalker;
    private final UpdatePasswordUseCase updatePassword;
    private final UpdateProfilePhotoUseCase updateProfilePhoto;

    public TalkerResource(CreateTalkerUseCase createTalker,
                          AuthenticateTalkerUseCase authenticateTalker,
                          TalkerProfileUseCase talkerProfile, UpdateTalkerUseCase updateTalker,
                          UpdatePasswordUseCase updatePassword, UpdateProfilePhotoUseCase updateProfilePhoto) {
        this.createTalker = createTalker;
        this.authenticateTalker = authenticateTalker;
        this.talkerProfile = talkerProfile;
        this.updateTalker = updateTalker;
        this.updatePassword = updatePassword;
        this.updateProfilePhoto = updateProfilePhoto;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateTalker requestBody) {
        this.createTalker.execute(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid AuthRequest requestBody) {
        return ResponseEntity.ok(this.authenticateTalker.execute(requestBody));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<TalkerProfile> profile(JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        return ResponseEntity.ok(this.talkerProfile.execute(id));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UpdateTalker requestBody,
                                       JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.updateTalker.execute(id, requestBody);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatedPassword requestBody,
                                               JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.updatePassword.execute(id, requestBody);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/profile-photo", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateProfilePhoto(@ModelAttribute @Valid UpdateProfilePhoto requestBody,
                                                   JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.updateProfilePhoto.execute(id, requestBody);
        return ResponseEntity.noContent().build();
    }
}
