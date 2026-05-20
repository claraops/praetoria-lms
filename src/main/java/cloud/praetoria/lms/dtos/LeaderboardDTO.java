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
public class LeaderboardDTO {
    private List<LeaderboardEntryDTO> entries;
    private LeaderboardEntryDTO userRank;
    private Integer totalUsers;
}
