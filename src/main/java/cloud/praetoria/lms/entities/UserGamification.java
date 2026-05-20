package cloud.praetoria.lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_gamification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserGamification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "total_xp", nullable = false)
    @Builder.Default
    private Integer totalXp = 0;

    @Column(name = "level", nullable = false)
    @Builder.Default
    private Integer level = 1;

    @Column(name = "streak_days", nullable = false)
    @Builder.Default
    private Integer streakDays = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "longest_streak", nullable = false)
    @Builder.Default
    private Integer longestStreak = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Recalcule le niveau en fonction du total XP
     * Formule: niveau = (totalXp / 100) + 1
     */
    public void updateLevel() {
        this.level = (this.totalXp / 100) + 1;
    }

    /**
     * Met à jour le streak quotidien
     */
    public void updateStreak() {
        LocalDate today = LocalDate.now();
        
        if (this.lastActivityDate == null) {
            
            this.streakDays = 1;
        } else if (this.lastActivityDate.equals(today)) {
            return;
        } else if (this.lastActivityDate.equals(today.minusDays(1))) {
            this.streakDays++;
        } else {
            this.streakDays = 1;
        }
        if (this.streakDays > this.longestStreak) {
            this.longestStreak = this.streakDays;
        }
        
        this.lastActivityDate = today;
    }

    /**
     * Ajout des XP et met à jour le niveau
     */
    public void addXp(int xpToAdd) {
        this.totalXp += xpToAdd;
        updateLevel();
    }
}