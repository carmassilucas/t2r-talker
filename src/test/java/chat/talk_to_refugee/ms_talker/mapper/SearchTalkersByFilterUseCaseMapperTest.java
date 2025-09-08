package chat.talk_to_refugee.ms_talker.mapper;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SearchTalkersByFilterUseCaseMapperTest {

    private SearchTalkersByFilterMapper mapper;

    @BeforeEach
    void set_up() {
        this.mapper = new SearchTalkersByFilterMapperImpl();
    }

    @Test
    @DisplayName("Deve ser possível recuperar valores da busca")
    void should_be_possible_retrieve_search_values() {
        var uuid = UUID.randomUUID();
        var birthDate = LocalDate.parse("2000-01-01");
        var type = TalkerType.Values.COLLABORATOR.get();

        var talker = new Talker();
        talker.setId(uuid);
        talker.setFullName("full name");
        talker.setProfilePhoto("profile photo");
        talker.setAboutMe("about me");
        talker.setBirthDate(birthDate);
        talker.setCurrentlyCity("currently city");
        talker.setCurrentlyState("currently state");
        talker.setType(type);

        var age = this.mapper.mapAge(birthDate);
        var expected = new SearchTalkersResponse(
                uuid, "full name", "profile photo", "about me", age, "currently state", "currently city", type
        );

        var response = this.mapper.toSearchResponse(talker);

        assertNotNull(response);
        assertEquals(expected, response);
    }
}