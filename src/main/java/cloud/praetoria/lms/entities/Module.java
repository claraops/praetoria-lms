package cloud.praetoria.lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules", indexes = {
        @Index(name = "idx_module_name", columnList = "name", unique = true),
        @Index(name = "idx_module_block", columnList = "block_id"),
        @Index(name = "idx_module_quiz", columnList = "quiz_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Exercise> exercises = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private Quiz quiz;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
