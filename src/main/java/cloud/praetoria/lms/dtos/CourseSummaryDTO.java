package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryDTO {
    private Long courseId;
    private String name;
    private String description;
    private String content;
    private String videoUrl;
    private Boolean isCompleted;
    private String startedAt;
    private String completedAt;
}
