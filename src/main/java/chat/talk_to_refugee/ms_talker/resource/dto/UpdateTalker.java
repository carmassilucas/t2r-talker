package chat.talk_to_refugee.ms_talker.resource.dto;

import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class UpdateTalker {

    private String fullName;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String birthDate;

    private String aboutMe;
    private String currentlyState;
    private String currentlyCity;
    private String type;

    public UpdateTalker() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getCurrentlyState() {
        return currentlyState;
    }

    public void setCurrentlyState(String currentlyState) {
        this.currentlyState = currentlyState;
    }

    public String getCurrentlyCity() {
        return currentlyCity;
    }

    public void setCurrentlyCity(String currentlyCity) {
        this.currentlyCity = currentlyCity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UpdateTalker that = (UpdateTalker) o;
        return Objects.equals(fullName, that.fullName) && Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(aboutMe, that.aboutMe) && Objects.equals(currentlyCity, that.currentlyCity) &&
                Objects.equals(currentlyState, that.currentlyState) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, birthDate, aboutMe, currentlyCity, currentlyState, type);
    }
}
