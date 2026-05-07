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

    private Integer totalXp;
    private Integer currentLevel;
 
    private Long totalCoursesStarted;
    private Long totalCoursesCompleted;
    private Double coursesCompletionRate;

    private Long totalExercisesCompleted;
    private Long totalExercisesStarted;
    private Double exercisesCompletionRate;

    private Long totalQuizzesCompleted;
    private Long totalQuizzesStarted;
    private Double quizzesCompletionRate;
    private Double averageQuizScore;

    private Long totalModules;
    private Long completedModules;
    private Double overallPercentComplete;

    private String nextRecommendedAction;
}