package chat.talk_to_refugee.ms_talker.mapper;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.entity.TalkerType;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateTalker;
import org.mapstruct.*;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UpdateTalkerMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(source = "birthDate", target = "birthDate", qualifiedByName = "mapBirthDate"),
            @Mapping(source = "type", target = "type", qualifiedByName = "mapType")
    })
    Talker updateTalker(UpdateTalker updateTalker, @MappingTarget Talker talker);

    @Condition
    default boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Named("mapType")
    default TalkerType mapType(String type) {
        return TalkerType.Values.valueOf(type).get();
    }

    @Named("mapBirthDate")
    default LocalDate mapBirthDate(String birthDate) {
        return LocalDate.parse(birthDate);
    }
}
