package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverallProgressDTO {
    private Long userId;
    private String userName;
    
    // Statistiques générales
    private Integer totalXp;
    private Integer currentLevel;
    
    // Statistiques cours
    private Long totalCoursesStarted;
    private Long totalCoursesCompleted;
    private Double coursesCompletionRate;
    
    // Statistiques exercices
    private Long totalExercisesCompleted;
    private Long totalExercisesStarted;
    private Double exercisesCompletionRate;
    
    // Statistiques quiz
    private Long totalQuizzesCompleted;
    private Long totalQuizzesStarted;
    private Double quizzesCompletionRate;
    private Double averageQuizScore;
    
    // Statistiques modules
    private Long totalModules;
    private Long completedModules;
    private Double overallPercentComplete;
    
    // Prochaine étape recommandée
    private String nextRecommendedAction;
}