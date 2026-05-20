package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressDTO {
    private Long moduleId;
    private String moduleName;
    private Long blockId;
    private String blockName;

    private Integer totalCourses;
    private Integer completedCourses;

    private Integer totalExercises;
    private Integer completedExercises;

    private Boolean hasQuiz;
    private Long quizId;
    private Boolean quizCompleted;
    private Integer quizScore;
    private Integer quizAttempts;

    private Double percentComplete;
    private Boolean isCompleted;
}