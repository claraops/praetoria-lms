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
    
    // Statistiques cours
    private Integer totalCourses;
    private Integer completedCourses;
    
    // Statistiques exercices
    private Integer totalExercises;
    private Integer completedExercises;
    
    // Quiz
    private Boolean hasQuiz;
    private Long quizId;
    private Boolean quizCompleted;
    private Integer quizScore;
    private Integer quizAttempts;
    
    // Progression globale du module
    private Double percentComplete;
    private Boolean isCompleted;
}