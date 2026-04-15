package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.Module;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleResponse {

    private Long id;
    private String name;
    private String description;
    private Long blockId;
    private String blockName;
    private Long courseCount;
    private Long exerciseCount;
    private Boolean hasQuiz;
    private List<CourseResponse> courses;
    private List<ExerciseResponse> exercises;
    private QuizResponse quiz;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un ModuleResponse à partir de l'entité Module
     * sans les listes de cours, exercices et quiz.
     */
    public static ModuleResponse fromEntity(Module module) {
        if (module == null) return null;
        
        return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .blockId(module.getBlock() != null ? module.getBlock().getId() : null)
                .blockName(module.getBlock() != null ? module.getBlock().getName() : null)
                .courseCount((long) module.getCourses().size())
                .exerciseCount((long) module.getExercises().size())
                .hasQuiz(module.getQuiz() != null)
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }

    /**
     * Construit un ModuleResponse enrichi avec les cours, exercices et quiz.
     */
    public static ModuleResponse fromEntityWithDetails(Module module) {
        if (module == null) return null;
        
        List<CourseResponse> courseResponses = module.getCourses().stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());

        List<ExerciseResponse> exerciseResponses = module.getExercises().stream()
                .map(ExerciseResponse::fromEntity)
                .collect(Collectors.toList());

        QuizResponse quizResponse = module.getQuiz() != null
                ? QuizResponse.fromEntity(module.getQuiz())
                : null;

        return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .blockId(module.getBlock() != null ? module.getBlock().getId() : null)
                .blockName(module.getBlock() != null ? module.getBlock().getName() : null)
                .courseCount((long) module.getCourses().size())
                .exerciseCount((long) module.getExercises().size())
                .hasQuiz(module.getQuiz() != null)
                .courses(courseResponses)
                .exercises(exerciseResponses)
                .quiz(quizResponse)
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }
}