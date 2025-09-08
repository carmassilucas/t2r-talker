package chat.talk_to_refugee.ms_talker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_talker")
public class Talker implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "about_me", columnDefinition = "text")
    private String aboutMe;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "currently_city")
    private String currentlyCity;

    @Column(name = "currently_state")
    private String currentlyState;

    @Column(name = "email", unique = true, nullable = false, updatable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "talker_type_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private TalkerType type;

    @Column(name = "active", nullable = false)
    private Boolean active;

    public Talker() {
    }

    public Talker(String fullName, LocalDate birthDate, String email, String password, TalkerType type) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCurrentlyCity() {
        return currentlyCity;
    }

    public void setCurrentlyCity(String currentlyCity) {
        this.currentlyCity = currentlyCity;
    }

    public String getCurrentlyState() {
        return currentlyState;
    }

    public void setCurrentlyState(String currentlyState) {
        this.currentlyState = currentlyState;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TalkerType getType() {
        return type;
    }

    public void setType(TalkerType type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Talker talker = (Talker) o;
        return Objects.equals(id, talker.id) && Objects.equals(fullName, talker.fullName) &&
                Objects.equals(profilePhoto, talker.profilePhoto) && Objects.equals(aboutMe, talker.aboutMe) &&
                Objects.equals(birthDate, talker.birthDate) && Objects.equals(currentlyCity, talker.currentlyCity) &&
                Objects.equals(currentlyState, talker.currentlyState) && Objects.equals(email, talker.email) &&
                Objects.equals(password, talker.password) && Objects.equals(createdAt, talker.createdAt) &&
                Objects.equals(updatedAt, talker.updatedAt) && Objects.equals(type, talker.type) &&
                Objects.equals(active, talker.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, profilePhoto, aboutMe, birthDate, currentlyCity, currentlyState,
                email, password, createdAt, updatedAt, type, active);
    }
}
