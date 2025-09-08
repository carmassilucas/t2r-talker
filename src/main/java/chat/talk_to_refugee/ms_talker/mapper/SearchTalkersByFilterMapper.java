package chat.talk_to_refugee.ms_talker.mapper;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.resource.dto.SearchTalkersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface SearchTalkersByFilterMapper {

    @Mapping(source = "birthDate", target = "age", qualifiedByName = "mapAge")
    SearchTalkersResponse toSearchResponse(Talker talker);

    @Named("mapAge")
    default Integer mapAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
