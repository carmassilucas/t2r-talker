package chat.talk_to_refugee.ms_talker.repository;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TalkerRepository extends JpaRepository<Talker, UUID> {

    Optional<Talker> findByEmail(String email);
}
