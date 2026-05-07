package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private Long badgeId;
    private String name;
    private String displayName;
    private String description;
    private String iconUrl;
    private String criteria;
    private Boolean earned;
    private String earnedAt;
}