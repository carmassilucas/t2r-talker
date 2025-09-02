package chat.talk_to_refugee.ms_talker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_talker_type")
public class TalkerType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",  unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "description", unique = true, nullable = false, updatable = false)
    private String description;

    public TalkerType() {

    }

    public TalkerType(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public enum Values {

        COLLABORATOR(1L, "collaborator"),
        IMMIGRANT(2L, "immigrant"),
        REFUGEE(3L, "refugee");

        private final Long id;
        private final String description;

        Values(Long id, String description) {
            this.id = id;
            this.description = description;
        }

        public TalkerType get() {
            return new TalkerType(id, description);
        }
    }
}
