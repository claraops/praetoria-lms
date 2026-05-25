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
public class ModuleDetailDTO {
    private Long moduleId;
    private String moduleName;
    private String description;
    private Long blockId;
    private String blockName;
    private Boolean isCompleted;
    private Double percentComplete;
    private List<CourseSummaryDTO> courses;
    private List<ExerciseSummaryDTO> exercises;
    private QuizSummaryDTO quiz;
}