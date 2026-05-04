package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private Integer rank;
    private Long userId;
    private String userName;
    private String userEmail;
    private String organizationName;
    private Integer totalXp;
    private Integer level;
    private Long badgeCount;
}
