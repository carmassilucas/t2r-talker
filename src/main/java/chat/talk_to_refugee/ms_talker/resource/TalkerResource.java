package chat.talk_to_refugee.ms_talker.resource;

import chat.talk_to_refugee.ms_talker.resource.dto.AuthRequest;
import chat.talk_to_refugee.ms_talker.resource.dto.AuthResponse;
import chat.talk_to_refugee.ms_talker.resource.dto.CreateTalker;
import chat.talk_to_refugee.ms_talker.service.TalkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/talkers")
public class TalkerResource {
    
    private final TalkerService service;

    public TalkerResource(TalkerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateTalker requestBody) {
        this.service.create(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody @Valid AuthRequest requestBody) {
        return ResponseEntity.ok(this.service.auth(requestBody));
    }
}
