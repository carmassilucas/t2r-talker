package chat.talk_to_refugee.ms_talker.repository;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TalkerRepository extends JpaRepository<Talker, UUID> {

    Optional<Talker> findByEmail(String email);

    @Query(
            nativeQuery = true,
            value = "select * " +
                    "from tb_talker as t " +
                    "where (:full_name is null or t.full_name ilike concat('%', :full_name, '%')) " +
                        "and (:currently_state is null or t.currently_state = :currently_state) " +
                        "and (:currently_city is null or t.currently_city = :currently_city) " +
                        "and (:type is null or t.talker_type_id in (:type)) " +
                        "and t.active = true " +
                        "and t.id <> :id"
    )
    Page<Talker> findByNonBlankFilters(@Param("id") UUID id,
                                       @Param("full_name") String fullName,
                                       @Param("currently_state") String currentlyState,
                                       @Param("currently_city") String currentlyCity,
                                       @Param("type") List<Long> type,
                                       Pageable pageable);
}
