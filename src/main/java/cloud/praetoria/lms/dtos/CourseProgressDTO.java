package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressDTO {
    private Long courseId;
    private String courseName;
    private Boolean completed;
    private String completedAt;
    private String startedAt;
    private Long moduleId;
    private String moduleName;
}