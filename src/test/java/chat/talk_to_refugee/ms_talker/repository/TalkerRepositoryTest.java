package chat.talk_to_refugee.ms_talker.repository;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TalkerRepositoryTest {

    @Autowired
    private TalkerRepository repository;

    @BeforeAll
    void before_all() {
        var talker = this.repository.save(new Talker(
                "John Doe",
                LocalDate.parse("2000-01-01"),
                "john.doe@email.com",
                "john.doe",
                TalkerType.Values.COLLABORATOR.get()
        ));

        talker.setCurrentlyState("currently state");
        talker.setCurrentlyCity("currently city");
        this.repository.save(talker);

        this.repository.save(new Talker(
                "Maria Silva",
                LocalDate.parse("1995-05-20"),
                "maria.silva@email.com",
                "maria.silva",
                TalkerType.Values.IMMIGRANT.get()
        ));

        this.repository.save(new Talker(
                "Carlos Oliveira",
                LocalDate.parse("1988-08-15"),
                "carlos.oliveira@email.com",
                "carlos.oliveira",
                TalkerType.Values.REFUGEE.get()
        ));
    }

    @Test
    @DisplayName("Deve ser possível encontrar um talker pelo e-mail")
    void should_be_possible_find_talker_by_email() {
        assertThat(this.repository.findByEmail("john.doe@email.com")).isNotNull().isPresent();
    }

    @Test
    @DisplayName("Deve retornar vazio quando talker não encontrado")
    void should_return_empty_when_talker_not_found() {
        assertThat(this.repository.findByEmail("not.found@email.com")).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve ser possível filtrar talker de acordo com filtros")
    void should_be_possible_filter_talkers_accordingly_to_filters() {
        var uuid = UUID.randomUUID();
        var unpaged = Pageable.unpaged();

        assertThat(this.repository.findByNonBlankFilters(
                uuid, null, null, null, null, unpaged
        )).isNotNull().size().isEqualTo(3);

        assertThat(this.repository.findByNonBlankFilters(
                uuid, "Doe", null, null, null, unpaged
        )).isNotNull().size().isEqualTo(1);

        assertThat(this.repository.findByNonBlankFilters(
                uuid, null, "currently state", "currently city", null, unpaged
        )).isNotNull().size().isEqualTo(1);

        var types = List.of(TalkerType.Values.COLLABORATOR.getId());
        assertThat(this.repository.findByNonBlankFilters(
                uuid, null, null, null, types, unpaged
        )).isNotNull().size().isEqualTo(1);

        assertThat(this.repository.findByNonBlankFilters(
                uuid, "Doe", "currently state", "currently city", types, unpaged
        )).isNotNull().size().isEqualTo(1);
    }
}