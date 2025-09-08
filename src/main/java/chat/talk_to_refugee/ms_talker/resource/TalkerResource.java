package chat.talk_to_refugee.ms_talker.resource;

import chat.talk_to_refugee.ms_talker.resource.dto.*;
import chat.talk_to_refugee.ms_talker.usecase.facade.TalkerFacade;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/talkers")
public class TalkerResource {

    private final TalkerFacade useCases;

    public TalkerResource(TalkerFacade useCases) {
        this.useCases = useCases;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateTalker requestBody) {
        this.useCases.create().execute(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid AuthRequest requestBody) {
        return ResponseEntity.ok(this.useCases.authenticate().execute(requestBody));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<TalkerProfile> profile(JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        return ResponseEntity.ok(this.useCases.profile().execute(id));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UpdateTalker requestBody,
                                       JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.useCases.update().execute(id, requestBody);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatedPassword requestBody,
                                               JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.useCases.updatePassword().execute(id, requestBody);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/profile-photo", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateProfilePhoto(@ModelAttribute @Valid UpdateProfilePhoto requestBody,
                                                   JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.useCases.updatePhoto().execute(id, requestBody);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/activate")
    public ResponseEntity<Void> activate(JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.useCases.toggleActive().execute(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/disable")
    public ResponseEntity<Void> disable(JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        this.useCases.toggleActive().execute(id, false);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<SearchTalkersResponse>> search(@ModelAttribute SearchTalkersRequest requestParams,
                                                              JwtAuthenticationToken token) {
        var id = UUID.fromString(token.getName());
        return ResponseEntity.ok(
                this.useCases.search().execute(id, requestParams)
        );
    }
}
