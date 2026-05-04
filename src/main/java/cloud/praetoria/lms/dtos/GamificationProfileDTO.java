package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamificationProfileDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer totalXp;
    private Integer level;
    private Integer streakDays;
    private Integer longestStreak;
    private String lastActivityDate;
    private Integer totalBadges;
    private Integer xpToNextLevel;
    private Integer progressToNextLevel;
    private List<BadgeDTO> badges;
}