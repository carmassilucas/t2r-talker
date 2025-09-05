package chat.talk_to_refugee.ms_talker.mapper;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateTalkerMapperTest {

    private UpdateTalkerMapper updateTalkerMapper;

    @BeforeEach
    void set_up() {
        this.updateTalkerMapper = new UpdateTalkerMapperImpl();
    }

    @Test
    @DisplayName("Deve ser possível atualizar valores preenchidos")
    void should_be_possible_update_filled_values() {
        var updateTalker = new UpdateTalker();
        updateTalker.setFullName("updated full name");
        updateTalker.setBirthDate("2000-01-01");
        updateTalker.setAboutMe("updated about me");
        updateTalker.setCurrentlyState("updated currently state");
        updateTalker.setCurrentlyCity("updated currently city");
        updateTalker.setType("immigrant");

        var talker = new Talker();
        talker.setFullName("full name");
        talker.setBirthDate(LocalDate.now());
        talker.setAboutMe("about me");
        talker.setCurrentlyState("currently state");
        talker.setCurrentlyCity("currently city");
        talker.setType(TalkerType.Values.COLLABORATOR.get());

        var expected = new Talker();
        expected.setFullName("updated full name");
        expected.setBirthDate(LocalDate.parse("2000-01-01"));
        expected.setAboutMe("updated about me");
        expected.setCurrentlyState("updated currently state");
        expected.setCurrentlyCity("updated currently city");
        expected.setType(TalkerType.Values.IMMIGRANT.get());

        var updated = this.updateTalkerMapper.updateTalker(updateTalker, talker);

        assertNotNull(updated);
        assertEquals(expected, updated);
    }

    @Test
    @DisplayName("Deve não ser possível atualizar valores nulos")
    void should_not_be_possible_update_null_values() {
        var updateTalker = new UpdateTalker();

        var talker = new Talker();
        talker.setFullName("full name");

        var updated = updateTalkerMapper.updateTalker(updateTalker, talker);

        assertEquals("full name", updated.getFullName());
    }
}